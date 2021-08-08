package com.sq26.experience.ui.activity.file

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.Settings
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.databinding.ActivityFileHomeBinding
import com.sq26.experience.databinding.ItemFileHoemRootBinding
import com.sq26.experience.util.i
import com.sq26.experience.util.permissions.JPermissions
import com.sq26.experience.util.result.OpenDocumentTree
import com.sq26.experience.util.result.StartActivityForResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

@AndroidEntryPoint
class FileHomeActivity : AppCompatActivity() {

    //请求目录权限
    private val request =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            //授予打开的文档树永久性的读写权限
            contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            //获取documentFile对象
            val documentFile = DocumentFile.fromTreeUri(this, uri)
            documentFile?.let {
                lifecycleScope.launch(Dispatchers.IO) {
                    //保存目录信息
//                    fileRootDao.insert(
//                        FileRoot(
//                            //获取分区名称
//                            it.name.toString(),
//                            //总大小
//                            Formatter.formatFileSize(this@FileHomeActivity, it.length()),
//                            //获取uri
//                            uri.toString(),
//                            //获取是否有读权限
//                            it.canRead(),
//                            //获取是否有写权限
//                            it.canWrite()
//                        )
//                    )
                }
            }
        }

//    val req = registerForActivityResult(ActivityResultContracts.)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityFileHomeBinding>(this, R.layout.activity_file_home)
            .apply {
                lifecycleOwner = this@FileHomeActivity
                toolbar.setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
                toolbar.menu.add(R.string.add).apply {
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    //设置图标
                    icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_add_24, theme)
                    //设置点击事件
                    setOnMenuItemClickListener {
                        //设置null表示不指定打开的目录,若要指定需要设置目录的uri
//                        request.launch(null)
                        OpenDocumentTree(supportFragmentManager)
                            .launch {

                            }
//                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
//                        //标识同时获取其子目录的读写权限
//                        intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
//                        startActivityForResult(intent, DOCUMENT_TREE_CODE)
                        true
                    }
                }

                val rootFiles = mutableListOf<File>()

                //type值是获取的文件夹的名字,null指应用文件的根目录,/storage/emulated/0/Android/data/com.sq26.experience/files
                val files = getExternalFilesDirs(null)

                files.forEach {
                    val path = it.absolutePath.substring(0, it.absolutePath.indexOf("/Android"))
                    rootFiles.add(File(path))
                }


                val adapter = object : RecyclerView.Adapter<CommonListViewHolder<*>>() {
                    override fun onCreateViewHolder(
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
//                                        val intent =
//                                            Intent(
//                                                this@FileHomeActivity,
//                                                FileOperateActivity::class.java
//                                            )
//                                        //在新的窗口打开
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
//                                        //设置根目录路径
//                                        intent.putExtra("rootPath", it.absolutePath)
//                                        //打开文件操作界面
//                                        startActivity(intent)

                                    }

                                }
                            }

                            override fun bind(position: Int) {
                                v.item = rootFiles[position]
                            }
                        }
                    }

                    override fun onBindViewHolder(holder: CommonListViewHolder<*>, position: Int) {
                        holder.bind(position)
                    }

                    override fun getItemCount(): Int {
                        return rootFiles.size
                    }
                }
                //设置分割线
                rootFileRecyclerView.addItemDecoration(
                    DividerItemDecoration(
                        this@FileHomeActivity,
                        DividerItemDecoration.VERTICAL
                    )
                )
                rootFileRecyclerView.adapter = adapter

//                fileRootDao.queryAll().asLiveData().observe(this@FileHomeActivity) {
//                    adapter.submitList(it)
//                }

                setOpenAlbum {
                    startActivity(Intent(this@FileHomeActivity, FileImageActivity::class.java))
                }
            }

        initDate()
    }

    private fun initDate() {
        //获取已获取授权的目录
        contentResolver.persistedUriPermissions.forEach {
            it.uri.toString().i("授权2")
        }

        //获取文件管理权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.getStorageDirectory().absolutePath.i("getStorageDirectory")
            //android11即以上
            if (Environment.isExternalStorageManager()) {
                val files = getExternalFilesDirs(null)
                //遍历分区
                "开始遍历".i()
                for (file in files) {
                    val path = file.absolutePath.substring(0, file.absolutePath.indexOf("/Android"))
                    path.i("path")
                    val ff = File(path)
                    for (item in ff.listFiles() ?: arrayOf()) {
                        item.absolutePath.i("item")
                        item.canWrite().i("item写")
                        item.canRead().i("item读")
                    }
                }
            } else {
                StartActivityForResult(supportFragmentManager)
                    .launch(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                        data = Uri.parse("package:$packageName")
                    }) {
                        it.resultCode.i("it.resultCode")
                        if (Environment.isExternalStorageManager()) {
                            val files = getExternalFilesDirs(null)
                            //遍历分区
//                            "开始遍历".i()
//                            for (file in files) {
//                                val path = file.absolutePath.substring(
//                                    0,
//                                    file.absolutePath.indexOf("/Android")
//                                )
//                                path.i("path")
//                                val ff = File(path)
//                                for (item in ff.listFiles() ?: arrayOf()) {
//                                    item.absolutePath.i("item")
//                                    item.canWrite().i("item写")
//                                    item.canRead().i("item读")
//                                }
//                            }
                        } else {
                            "没授权".i()
                        }
                    }
//                ActivityResultContracts.StartActivityForResult
            }
        } else {
            //android10即以下
            // 先在AndroidManifest的application中设置requestLegacyExternalStorage = false 关闭分区储存
            //这样就可以通过申请写入权限来获取外置储存的读写权
            JPermissions(this@FileHomeActivity).success {
                //type值是获取的文件夹的名字,null指应用文件的根目录,/storage/emulated/0/Android/data/com.sq26.experience/files
                val files = getExternalFilesDirs(null)

//                for (file in files) {
//
//
//                    val path = file.absolutePath.substring(0, file.absolutePath.indexOf("/Android"))
//                    path.i("path")
//                    val ff = File(path)
//                    for (item in ff.listFiles() ?: arrayOf()) {
//                        item.absolutePath.i("item")
//                        item.canWrite().i("item写")
//                        item.canRead().i("item读")
//                    }
//                }

            }.start(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }
}