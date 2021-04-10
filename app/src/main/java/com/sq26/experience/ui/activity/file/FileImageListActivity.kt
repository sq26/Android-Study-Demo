package com.sq26.experience.ui.activity.file

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonListAdapter
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.databinding.ActivityFileImageListBinding
import com.sq26.experience.databinding.ItemFileImageBinding
import com.sq26.experience.viewmodel.FileImageViewModel
import java.text.SimpleDateFormat

class FileImageListActivity : AppCompatActivity() {
    private val viewModel: FileImageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityFileImageListBinding>(
            this,
            R.layout.activity_file_image_list
        )
            .apply {
                lifecycleOwner = this@FileImageListActivity

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

                recyclerView.adapter = imageAdapter

                viewModel.imageListFlow(intent.getIntExtra("index", 0))
                    .observe(this@FileImageListActivity) {
                        imageAdapter.submitList(it)
                    }
            }
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