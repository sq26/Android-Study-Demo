package com.sq26.experience.ui.activity.file

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.facebook.drawee.view.SimpleDraweeView
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonListAdapter
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.adapter.RecyclerViewListAdapter
import com.sq26.experience.adapter.ViewHolder
import com.sq26.experience.data.FileRoot
import com.sq26.experience.databinding.ActivityFileManagementBinding
import com.sq26.experience.databinding.ItemFileBinding
import com.sq26.experience.databinding.ItemParentFileBinding
import com.sq26.experience.util.FileUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Attributes

class FileOperateActivity : AppCompatActivity() {

    //父文件夹列表适配器
    private var parentFileListAdapter: RecyclerViewListAdapter<DocumentFile>? = null

    //父文件夹列表
    private val parentFileList: MutableList<DocumentFile> = ArrayList()

    //文件列表适配器
    private lateinit var fileListAdapter: CommonListAdapter<DocumentFile>

    //文件列表
    private val fileList: MutableList<DocumentFile> = ArrayList()

    //主储存的根目录路径
    private var rootPath: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityFileManagementBinding>(
            this,
            R.layout.activity_file_management
        )
            .apply {
                val parentFileListAdapter =
                    object : CommonListAdapter<DocumentFile>(DocumentFileDiffCallback()) {
                        override fun createView(
                            parent: ViewGroup,
                            viewType: Int
                        ): CommonListViewHolder<*> {
                            return object : CommonListViewHolder<ItemParentFileBinding>(
                                ItemParentFileBinding.inflate(
                                    LayoutInflater.from(parent.context),
                                    parent,
                                    false
                                )
                            ) {
                                override fun bind(position: Int) {
                                    v.documentFile = getItem(position)
                                    v.setOnClick {
                                        parentFileList.subList(position + 1, parentFileList.size).clear()
                                        submitList(parentFileList)
                                    }
                                    executePendingBindings()
                                }
                            }
                        }

                    }

                rootRecyclerView.adapter = parentFileListAdapter

                fileListAdapter =
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
                                override fun bind(position: Int) {
                                    val documentFile = getItem(position)
                                    v.name.text = documentFile.name
                                    v.name.setTextColor(if (documentFile.canWrite()) Color.GREEN else Color.RED)
                                    v.dateTime.text =
                                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(documentFile.lastModified())
                                    if (documentFile.isFile){
                                        v.simpleDraweeView.setImageResource(R.drawable.ic_insert_drive_file_black_24dp)
                                        v.remark.text = Formatter.formatFileSize(this@FileOperateActivity, documentFile.length())
                                    }else{
                                        v.simpleDraweeView.setImageResource(R.drawable.ic_folder_open_black_24dp)
                                        v.remark.text = "${documentFile.listFiles().size}个项目"
                                    }
                                    v.root.setOnClickListener {
                                        if (documentFile.isFile) {
                                            FileUtil.openFile(this@FileOperateActivity, documentFile.uri)
                                        } else {
                                            parentFileList.add(documentFile)
                                            parentFileListAdapter.submitList(parentFileList)

                                            fileListAdapter.submitList(listOf(*documentFile.listFiles()))
                                        }

                                    }
                                    v.root.setOnLongClickListener {
                                        AlertDialog.Builder(this@FileOperateActivity)
                                            .setMessage("是否删除")
                                            .setPositiveButton("是") { dialogInterface, i ->
                                                Log.d("删除", "有权限")
                                                documentFile.delete()
                                                fileList.remove(documentFile)
                                                fileListAdapter.submitList(fileList)
                                            }.show()
                                        true
                                    }
//                                    executePendingBindings()
                                }
                            }
                        }

                    }

                recyclerView.adapter = fileListAdapter
                //获取根目录路径
                rootPath = intent.getStringExtra("rootPath")!!
                //声明一个DocumentFile对象
                val documentFile = DocumentFile.fromTreeUri(this@FileOperateActivity, Uri.parse(rootPath))!!
                parentFileList.add(documentFile)
                parentFileListAdapter.submitList(parentFileList)
                //初始化数据
                fileListAdapter.submitList(listOf(*documentFile.listFiles()))
            }
    }

//    private fun initView() {
//        parentFileListAdapter = object :
//            RecyclerViewListAdapter<DocumentFile?>(R.layout.item_parent_file, parentFileList) {
//            override fun bindViewHolder(viewHolder: ViewHolder, item: DocumentFile, position: Int) {
//                viewHolder.setText(R.id.text, item.name)
//                viewHolder.setOnClickListener(R.id.text) { view: View? ->
//                    parentFileList.subList(position + 1, parentFileList.size).clear()
//                    notifyDataSetChanged()
//                    initData(item.listFiles())
//                }
//            }
//        }
//        parentRecyclerView!!.adapter = parentFileListAdapter
//
//        //文件列表适配器
//        fileListAdapter =
//            object : RecyclerViewListAdapter<DocumentFile?>(R.layout.item_file, fileList) {
//                override fun bindViewHolder(
//                    viewHolder: ViewHolder,
//                    item: DocumentFile,
//                    position: Int
//                ) {
//                    viewHolder.setText(R.id.name, item.name)
//                    viewHolder.setTextColor(
//                        R.id.name,
//                        if (item.canWrite()) Color.GREEN else Color.RED
//                    )
//                    viewHolder.setText(
//                        R.id.dateTime,
//                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item.lastModified())
//                    )
//                    val simpleDraweeView =
//                        viewHolder.getView<SimpleDraweeView>(R.id.simpleDraweeView)
//                    if (item.isFile) {
//                        simpleDraweeView.setImageResource(R.drawable.ic_insert_drive_file_black_24dp)
//                        viewHolder.setText(
//                            R.id.remark,
//                            Formatter.formatFileSize(context, item.length())
//                        )
//                    } else {
//                        simpleDraweeView.setImageResource(R.drawable.ic_folder_open_black_24dp)
//                        viewHolder.setText(R.id.remark, item.listFiles().size.toString() + "个项目")
//                    }
//                    viewHolder.itemView.setOnClickListener {
//                        if (item.isFile) {
//                            FileUtil.openFile(context, item.uri)
//                        } else {
//                            parentFileList.add(item)
//                            parentFileListAdapter.notifyDataSetChanged()
//                            initData(item.listFiles())
//                        }
//                    }
//                    viewHolder.itemView.setOnLongClickListener {
//                        AlertDialog.Builder(this@FileOperateActivity)
//                            .setMessage("是否删除")
//                            .setPositiveButton("是") { dialogInterface, i ->
//                                Log.d("删除", "有权限")
//                                item.delete()
//                                fileList.remove(item)
//                                fileListAdapter!!.notifyDataSetChanged()
//                            }.show()
//                        true
//                    }
//                }
//            }
//        //设置分割线
//        recyclerView!!.addItemDecoration(
//            DividerItemDecoration(
//                this,
//                DividerItemDecoration.VERTICAL
//            )
//        )
//        //设置适配器
//        recyclerView!!.adapter = fileListAdapter
//        //声明一个DocumentFile对象
//        val documentFile: DocumentFile?
//        //判断是否是绝对路径
//        documentFile = if (FileUtil.isAbsolutePath(rootPath)) {
//            //是绝对路径,要先转成File类
//            DocumentFile.fromFile(File(rootPath))
//        } else {
//            //不是绝对路径
//            DocumentFile.fromTreeUri(context!!, Uri.parse(rootPath))
//        }
//        parentFileList.add(documentFile)
//        parentFileListAdapter.notifyDataSetChanged()
//        //初始化数据
//        initData(documentFile!!.listFiles())
//    }

//    private fun initData(documentFiles: Array<DocumentFile>) {
//        fileListAdapter.submitList(listOf(*documentFiles))
//    }

    override fun onBackPressed() {
        if (parentFileList.size > 1) {
            parentFileList.removeAt(parentFileList.size - 1)
            parentFileListAdapter!!.notifyDataSetChanged()

            fileListAdapter.submitList(listOf(*parentFileList[parentFileList.size - 1].listFiles()))
        } else {
            super.onBackPressed()
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