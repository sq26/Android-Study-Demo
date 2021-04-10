package com.sq26.experience.ui.activity.file

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonListAdapter
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.databinding.ActivityFileImageDirectoryBinding
import com.sq26.experience.databinding.ItemFileImageTypeBinding
import com.sq26.experience.databinding.LayoutFileImageHeaderBinding
import com.sq26.experience.util.Log
import com.sq26.experience.util.permissions.JPermissions
import com.sq26.experience.viewmodel.FileImageViewModel

class FileImageDirectoryActivity : AppCompatActivity() {
    private val viewModel: FileImageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityFileImageDirectoryBinding>(
            this,
            R.layout.activity_file_image_directory
        ).apply {
            val adapter = object : CommonListAdapter<ImageType>(ImageTypeCallback) {
                override fun createView(
                    parent: ViewGroup,
                    viewType: Int
                ): CommonListViewHolder<*> {
                    return if (viewType == 0) {
                        object : CommonListViewHolder<LayoutFileImageHeaderBinding>(
                            LayoutFileImageHeaderBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
                        ) {
                            override fun bind(position: Int) {
                                Log.i(position, "he")
                            }
                        }
                    } else {
                        object : CommonListViewHolder<ItemFileImageTypeBinding>(
                            ItemFileImageTypeBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
                        ) {

                            override fun bind(position: Int) {
                                Log.i(position, "body")
                                val item = getItem(position)
                                v.item = item
                                executePendingBindings()
                                v.image.setImageURI(item.uri.toString())
                                v.setOnClick {
                                    startActivity(
                                        Intent(
                                            this@FileImageDirectoryActivity,
                                            FileImageListActivity::class.java
                                        ).apply {
                                            putExtra("index", position)
                                        })
                                }
                            }
                        }
                    }
                }

                override fun getItemCount(): Int {
                    return super.getItemCount() + 1
                }

                override fun getItem(position: Int): ImageType {
                    return super.getItem(position - 1)
                }

                override fun getItemViewType(position: Int): Int {
                    return if (position == 0) 0 else 1
                }
            }
            recyclerView.layoutManager =
                GridLayoutManager(this@FileImageDirectoryActivity, 2).also {
                    it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == 0)
                                it.spanCount
                            else
                                1
                        }

                    }
                }

            recyclerView.adapter = adapter

            viewModel.imagesTypeLiveData.observe(this@FileImageDirectoryActivity) {
                adapter.submitList(it)
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