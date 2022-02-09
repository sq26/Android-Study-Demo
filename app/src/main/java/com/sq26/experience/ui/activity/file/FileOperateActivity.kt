package com.sq26.experience.ui.activity.file

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonListAdapter
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.databinding.ActivityFileManagementBinding
import com.sq26.experience.databinding.ItemFileBinding
import com.sq26.experience.databinding.ItemParentFileBinding
import com.sq26.experience.util.FileUtil
import java.util.*

class FileOperateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityFileManagementBinding>(
            this,
            R.layout.activity_file_management
        )
            .apply {
                lifecycleOwner = this@FileOperateActivity

                //父文件夹列表
                val parentFileList: MutableList<DocumentFile> = ArrayList()
                //创建父文件夹目录适配器
                val parentFileListAdapter =
                    object : CommonListAdapter<DocumentFile>(DocumentFileDiffCallback()) {
                        override fun createView(
                            parent: ViewGroup,
                            viewType: Int
                        ): CommonListViewHolder<*> {
                            //创建通用视图持有者
                            return object : CommonListViewHolder<ItemParentFileBinding>(
                                ItemParentFileBinding.inflate(
                                    LayoutInflater.from(parent.context),
                                    parent,
                                    false
                                )
                            ) {
                                //绑定数据
                                override fun bind(position: Int) {
                                    //设置参数
                                    v.documentFile = getItem(position)
                                    //设置点击事件
                                    v.setOnClick {
                                        //移除从选定位置到结尾的目录
                                        parentFileList.subList(position + 1, parentFileList.size)
                                            .clear()
                                        //刷新视图
                                        submitList(parentFileList.toList())
                                    }
                                    //强制绑定数据
                                    executePendingBindings()
                                }
                            }
                        }

                    }
                //绑定适配器
                rootRecyclerView.adapter = parentFileListAdapter

                //文件列表
                val fileList: MutableList<DocumentFile> = ArrayList()
                //创建文件适配器
                val fileListAdapter =
                    object : CommonListAdapter<DocumentFile>(DocumentFileDiffCallback()) {
                        override fun createView(
                            parent: ViewGroup,
                            viewType: Int
                        ): CommonListViewHolder<*> {
                            return object : CommonListViewHolder<ItemFileBinding>(
                                ItemFileBinding.inflate(
                                    LayoutInflater.from(parent.context),
                                    parent,
                                    false
                                )
                            ) {
                                init {
                                    //设置根目录的点击事件
                                    v.root.setOnClickListener {
                                        v.item?.let {
                                            if (it.isFile) {
                                                FileUtil.openFile(
                                                    this@FileOperateActivity,
                                                    it.uri
                                                )
                                            } else {
                                                parentFileList.add(it)
                                                parentFileListAdapter.submitList(parentFileList.toList())
                                                submitList(it.listFiles().toList())
                                            }
                                        }
                                    }
                                    //设置根目录的长按事件
                                    v.root.setOnLongClickListener {
                                        v.item?.let {
                                            AlertDialog.Builder(this@FileOperateActivity)
                                                .setMessage("是否删除")
                                                .setPositiveButton("是") { _, _ ->
                                                    Log.d("删除", "有权限")
                                                    //删除数据
                                                    it.delete()
                                                    //移除对应的documentFile对象
                                                    fileList.remove(it)
                                                    //刷新视图
                                                    submitList(fileList.toList())
                                                }.show()
                                        }
                                        true
                                    }
                                }
                                override fun bind(position: Int) {
                                    v.item = getItem(position)
                                }
                            }
                        }

                    }
                //设置分割线
                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        this@FileOperateActivity,
                        DividerItemDecoration.VERTICAL
                    )
                )
                //绑定适配器
                recyclerView.adapter = fileListAdapter
                //获取根目录路径
                val rootUri = intent.getStringExtra("rootUri").orEmpty()
                //声明一个DocumentFile对象
                val documentFile =if (rootUri.toUri().scheme == ContentResolver.SCHEME_FILE){
                    DocumentFile.fromFile(rootUri.toUri().toFile())
                }else{
                    DocumentFile.fromTreeUri(this@FileOperateActivity, Uri.parse(rootUri))
                }

                documentFile?.let {
                    //加入父目录列表
                    parentFileList.add(it)
                    //刷新父目录视图
                    parentFileListAdapter.submitList(parentFileList.toList())
                    //初始化数据
                    fileListAdapter.submitList(it.listFiles().toList())
                }
                //添加返回键监听
                onBackPressedDispatcher.addCallback {
                    //parentFileList数量大于1说明不是根目录
                    if (parentFileList.size > 1) {
                        //移除最后一个目录
                        parentFileList.removeAt(parentFileList.size - 1)
                        //刷新父目录列表
                        parentFileListAdapter.submitList(parentFileList.toList())
                        //刷新文件列表
                        fileListAdapter.submitList(listOf(*parentFileList[parentFileList.size - 1].listFiles()))
                    } else {
                        //关闭返回监听
                        isEnabled = false
                        //主动调用返回
                        onBackPressedDispatcher.onBackPressed()
                        onBackPressedDispatcher.hasEnabledCallbacks()
                    }
                }
            }
    }
}

class DocumentFileDiffCallback : DiffUtil.ItemCallback<DocumentFile>() {
    //判断唯一标识是否相同
    override fun areItemsTheSame(
        oldItem: DocumentFile,
        newItem: DocumentFile
    ): Boolean {
        return oldItem.uri == newItem.uri
    }

    //判断内容是否相同
    override fun areContentsTheSame(
        oldItem: DocumentFile,
        newItem: DocumentFile
    ): Boolean {
        return oldItem.uri == oldItem.uri
    }

}