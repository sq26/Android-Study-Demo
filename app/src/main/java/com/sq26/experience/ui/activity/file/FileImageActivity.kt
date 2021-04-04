package com.sq26.experience.ui.activity.file

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonListAdapter
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.databinding.ActivityFileImageBinding
import com.sq26.experience.databinding.ItemFileImageBinding
import com.sq26.experience.databinding.ItemFileImageTypeBinding
import com.sq26.experience.util.FileUtil
import com.sq26.experience.util.Log
import com.sq26.experience.util.permissions.JPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class FileImageActivity : AppCompatActivity() {
    private val viewModel: FileImageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityFileImageBinding>(this, R.layout.activity_file_image)
            .apply {
                lifecycleOwner = this@FileImageActivity

                val imageAdapter = object : CommonListAdapter<ImageInfo>(ImageInfoCallback) {
                    override fun createView(
                        parent: ViewGroup,
                        viewType: Int
                    ): CommonListViewHolder<*> {
                        return object : CommonListViewHolder<ItemFileImageBinding>(
                            ItemFileImageBinding.inflate(LayoutInflater.from(parent.context))
                        ) {
                            override fun bind(position: Int) {
                                v.image.setImageURI(getItem(position).uri.toString())
                            }
                        }
                    }

                }

                val adapter = object : CommonListAdapter<ImageType>(ImageTypeCallback) {
                    override fun createView(
                        parent: ViewGroup,
                        viewType: Int
                    ): CommonListViewHolder<*> {
                        return object : CommonListViewHolder<ItemFileImageTypeBinding>(
                            ItemFileImageTypeBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
                        ) {

                            init {
                                v.setOnClick {
                                    v.item?.let {
                                        imageAdapter.submitList(it.list)
                                    }
                                    recyclerView.layoutManager =
                                        GridLayoutManager(this@FileImageActivity, 3)

                                    recyclerView.adapter = imageAdapter

                                }
                            }

                            override fun bind(position: Int) {
                                val item = getItem(position)
                                v.item = item
                                executePendingBindings()
                                v.image.setImageURI(item.uri.toString())
                            }
                        }

                    }

                }

                recyclerView.layoutManager = GridLayoutManager(this@FileImageActivity, 2)

                recyclerView.adapter = adapter

                viewModel.imagesTypeLiveData.observe(this@FileImageActivity) {
                    adapter.submitList(it)
                }

                onBackPressedDispatcher.addCallback {

                    if (recyclerView.adapter === adapter) {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    } else {
                        recyclerView.layoutManager = GridLayoutManager(this@FileImageActivity, 2)

                        recyclerView.adapter = adapter

//                        adapter.submitList(viewModel.imagesTypeLiveData.value)
                    }


                }
            }



        JPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
            .success {
                viewModel.initData(this)
            }
            .failure { _, _, _ ->

            }
            .start()
    }

}

class FileImageViewModel : ViewModel() {

    val imagesTypeLiveData = MutableLiveData<List<ImageType>>()


    fun initData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val list: MutableList<ImageInfo> = arrayListOf()
            //指定的是images,并且指明是外部内容
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        val uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI

            val query = context.contentResolver.query(uri, null, null, null, null)
            //use方法用于自动处理游标异常和自动关闭游标
            query?.use { cursor ->
                while (cursor.moveToNext()) {
                    Log.i(
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)),
                        "data"
                    )
                    val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                    val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
                    val displayName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                    val mimeType =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))
                    val title =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE))
                    val dateAdded =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
                    val dateModified =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))
                    val width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH))
                    val height =
                        cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT))
                    val bucketId =
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                        } else {
                            0
                        }
                    val bucketDisplayName =
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                        } else {
                            FileUtil.getParentFileName(
                                cursor.getString(
                                    cursor.getColumnIndex(
                                        MediaStore.Images.Media.DATA
                                    )
                                )
                            )
                        }
                    list.add(
                        ImageInfo(
                            id,
                            size,
                            displayName,
                            mimeType,
                            title,
                            dateAdded,
                            dateModified,
                            width,
                            height,
                            ContentUris.withAppendedId(uri, id),
                            bucketId,
                            bucketDisplayName
                        )
                    )
                }
            }

            val imagesTypeList = mutableListOf<ImageType>()
            list.forEach { item ->
                val index = imagesTypeList.indexOfFirst {
                    it.title == item.bucketDisplayName
                }
                if (index == -1) {
                    //不存在,需要创建
                    imagesTypeList.add(
                        ImageType(
                            item.bucketDisplayName,
                            item.uri,
                            mutableListOf(item)
                        )
                    )
                } else {
                    //已存在加数据
                    imagesTypeList[index].list.add(item)
                }
            }
            Log.i(imagesTypeList.toString())
            imagesTypeLiveData.postValue(imagesTypeList)

        }

    }
}

data class ImageType(
    //标题
    val title: String,
    //显示的图
    val uri: Uri,
    //子视图列表
    val list: MutableList<ImageInfo>
) {
    //数量
    val count: Int get() = list.count()
}

object ImageTypeCallback : DiffUtil.ItemCallback<ImageType>() {
    override fun areItemsTheSame(oldItem: ImageType, newItem: ImageType): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: ImageType, newItem: ImageType): Boolean {
        return oldItem == newItem
    }
}

data class ImageInfo(
    //文件id
    val id: Long,
    //文件大小
    val size: Long,
    //文件名称(带后缀)
    val displayName: String,
    //媒体类型(如:image/jpeg)
    val mimeType: String,
    //文件名(不带后缀)
    val title: String,
    //创建时间
    val dateAdded: Long,
    //修改时间
    val dateModified: Long,
    //宽度
    val width: Int,
    //高度
    val height: Int,
    //文件uri
    val uri: Uri,
    val bucketId: Long,
    //父目录名称
    val bucketDisplayName: String
) {
    //修改时间格式化
    val dateModifiedFormat: String
        get() = SimpleDateFormat.getDateTimeInstance().format(dateModified)
}

object ImageInfoCallback : DiffUtil.ItemCallback<ImageInfo>() {
    override fun areItemsTheSame(oldItem: ImageInfo, newItem: ImageInfo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ImageInfo, newItem: ImageInfo): Boolean {
        return oldItem == newItem
    }
}