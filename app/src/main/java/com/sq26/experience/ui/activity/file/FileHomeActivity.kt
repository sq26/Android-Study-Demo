package com.sq26.experience.ui.activity.file

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.Formatter
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonAdapter
import com.sq26.experience.adapter.CommonListAdapter
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.adapter.ViewHolder
import com.sq26.experience.data.RecyclerViewItem
import com.sq26.experience.databinding.ActivityFileHomeBinding
import com.sq26.experience.databinding.ItemFileHoemRootBinding
import com.sq26.experience.util.AppUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class FileHomeActivity : AppCompatActivity() {

    private val authorizeUriArray = JSONArray()
    private lateinit var context:Context

    private val request = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()){

                //授予打开的文档树永久性的读写权限
                contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                authorizeUriArray.add(it.toString())
                AppUtil.savePrivateFile(
                    context,
                    "authorizeUriArrayJson.json",
                    authorizeUriArray.toJSONString()
                )
                initDate()
    }
    private lateinit var adapter: CommonListAdapter<FileRoot>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        DataBindingUtil.setContentView<ActivityFileHomeBinding>(this, R.layout.activity_file_home)
            .apply {
                toolbar.setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
                toolbar.menu.add(R.string.add).apply {
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    icon = getDrawable(R.drawable.ic_baseline_add_24)
                    setOnMenuItemClickListener {
                        request.launch(null,)
//                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
//                        //标识同时获取其子目录的读写权限
//                        intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
//                        startActivityForResult(intent, DOCUMENT_TREE_CODE)
                        true
                    }
                }
                adapter = object : CommonListAdapter<FileRoot>(FileRootDiffCallback()) {
                    override fun createView(
                        parent: ViewGroup,
                        viewType: Int
                    ): CommonListViewHolder<*> {
                        return object : CommonListViewHolder<ItemFileHoemRootBinding>(
                            ItemFileHoemRootBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
                        ) {
                            init {
                                v.setOnClick {
                                    v.item?.let {
                                        //新建打开界面意图
                                        val intent =
                                            Intent(this@FileHomeActivity, FileOperateActivity::class.java)
                                        //在新的窗口打开
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                                        //设置根目录路径
                                        intent.putExtra("rootPath", it.uri)
                                        //打开文件操作界面
                                        startActivity(intent)
                                    }

                                }
                            }

                            override fun bind(position: Int) {
                                v.item = getItem(position)
                                v.executePendingBindings()
                            }
                        }
                    }
                }
                //设置分割线
                rootFileRecyclerView.addItemDecoration(
                    DividerItemDecoration(
                        context,
                        DividerItemDecoration.VERTICAL
                    )
                )
                rootFileRecyclerView.adapter = adapter

                setOpenAlbum {
                    startActivity(Intent(this@FileHomeActivity, FileImageActivity::class.java))
                }
            }

        initDate()
    }

    private fun initDate() {

        //type值是获取的文件夹的名字,null指应用文件的根目录,/storage/emulated/0/Android/data/com.sq26.experience/files
        val files = getExternalFilesDirs(null)
        val rootFileArray = mutableListOf<FileRoot>()
        //遍历分区
        for (file in files) {
            //获取根目录file对象,在Android设备装载的储存设备必然会在根目录创建一个名为Android 的文件夹,通过截取Android关键字获取根目录
            val documentFile = DocumentFile.fromFile(file)
            //新建一个json对象,分区信息
            val jsonObject = JSONObject()
            //获取分区名称
            jsonObject["name"] = documentFile.name
            //获取已用大小和总大小
            jsonObject["rom"] = Formatter.formatFileSize(context, documentFile.length())
            //获取绝对路径
            jsonObject["path"] = file.absolutePath
            //获取是否有读权限
            jsonObject["canRead"] = documentFile.canRead()
            //获取是否有写权限
            jsonObject["canWrite"] = documentFile.canWrite()
            //添加入储存空间列表
            rootFileArray.add(
                FileRoot(
                    documentFile.name ?: "",
                    Formatter.formatFileSize(context, documentFile.length()),
                    file.absolutePath,
                    documentFile.canRead(),
                    documentFile.canWrite()
                )
            )
        }

        //获取所有已有授权的路径和url的键值对
        val authorizeUriArrayJson = AppUtil.loadPrivateFile(context, "authorizeUriArrayJson.json")
        //判断有没有保存过键值对
        if (!authorizeUriArrayJson.isEmpty()) {
            //设置到私有对象
            authorizeUriArray.clear()
            authorizeUriArray.addAll(JSON.parseArray(authorizeUriArrayJson))
        }
        for (urlString in authorizeUriArray.toJavaList(String::class.java)) {
            val documentFile = DocumentFile.fromTreeUri(this, Uri.parse(urlString))
            //新建一个json对象,分区信息
            val jsonObject = JSONObject()
            //获取分区名称
            jsonObject["name"] = Objects.requireNonNull(documentFile)!!.name
            //获取已用大小和总大小
            jsonObject["rom"] = Formatter.formatFileSize(context, documentFile!!.length())
            //获取绝对路径
            jsonObject["path"] = documentFile.uri.toString()
            //获取是否有读权限
            jsonObject["canRead"] = documentFile.canRead()
            //获取是否有写权限
            jsonObject["canWrite"] = documentFile.canWrite()
            //添加入储存空间列表
            rootFileArray.add(FileRoot(
                documentFile.name ?: "",
                Formatter.formatFileSize(context, documentFile.length()),
                documentFile.uri.toString(),
                documentFile.canRead(),
                documentFile.canWrite()
            ))
        }
        //刷新分区列表
        adapter.submitList(rootFileArray)
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            if (requestCode == DOCUMENT_TREE_CODE) { //授予打开的文档树永久性的读写权限
//                contentResolver.takePersistableUriPermission(
//                    data!!.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION or
//                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                )
//                authorizeUriArray.add(data.data.toString())
//                AppUtil.savePrivateFile(
//                    context,
//                    "authorizeUriArrayJson.json",
//                    authorizeUriArray.toJSONString()
//                )
//                initDate()
//            }
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> onBackPressed()
//            R.id.action_add -> {
//                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
//                //标识同时获取其子目录的读写权限
//                intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
//                startActivityForResult(intent, DOCUMENT_TREE_CODE)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
}

data class FileRoot(
    //分区名称
    val name: String,
    //总大小
    val rom: String,
    //路径
    val uri: String,
    //是否有读权限
    val canRead: Boolean,
    //是否有写权限
    val canWrite: Boolean
)

class FileRootDiffCallback : DiffUtil.ItemCallback<FileRoot>() {
    //判断唯一标识是否相同
    override fun areItemsTheSame(
        oldItem: FileRoot,
        newItem: FileRoot
    ): Boolean {
        return oldItem.uri == newItem.uri
    }

    //判断内容是否相同
    override fun areContentsTheSame(
        oldItem: FileRoot,
        newItem: FileRoot
    ): Boolean {
        return oldItem == newItem
    }

}