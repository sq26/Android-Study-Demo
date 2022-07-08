package com.sq26.experience.ui.activity.file

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
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
import kotlin.math.log

class FileHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityFileHomeBinding>(this, R.layout.activity_file_home)
            .apply {
                lifecycleOwner = this@FileHomeActivity
                toolbar.setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }

                val rootFiles = mutableListOf<DocumentFile>()

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
                                        val intent =
                                            Intent(
                                                this@FileHomeActivity,
                                                FileOperateActivity::class.java
                                            )
                                        //在新的窗口打开
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                                        //设置根目录路径
                                        intent.putExtra("rootUri", it.uri.toString())
                                        //打开文件操作界面
                                        startActivity(intent)

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
                //刷新列表,为兼容以后的Android版本最好把外部文件操作全部统一为documentFile类,内部分区储存空间可以继续file类
                fun refresh() {
                    rootFiles.clear()
                    //type值是获取的文件夹的名字,null指应用文件的根目录,/storage/emulated/0/Android/data/com.sq26.experience/files
                    val file = getExternalFilesDir(null)
                    file?.let {
                        val path = it.absolutePath.substring(0, it.absolutePath.indexOf("/Android"))
                        rootFiles.add(DocumentFile.fromFile(File(path)))
                    }

                    //获取已获取授权的目录
                    contentResolver.persistedUriPermissions.forEach {
                        DocumentFile.fromTreeUri(this@FileHomeActivity, it.uri)?.let { it1 ->
                            rootFiles.add(it1)
                        }
                    }
                }

                toolbar.menu.add(R.string.add).apply {
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    //设置图标
                    icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_add_24, theme)
                    //设置点击事件
                    setOnMenuItemClickListener {
                        OpenDocumentTree(supportFragmentManager)
                            .launch {
                                refresh()
                            }
                        true
                    }
                }

                //获取外部储存目录文件管理权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    //android11即以上
                    if (Environment.isExternalStorageManager()) {
                        refresh()
                    } else {
                        StartActivityForResult(supportFragmentManager)
                            .launch(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                data = Uri.parse("package:$packageName")
                            }) {
                                it.resultCode.i("it.resultCode")
                                if (Environment.isExternalStorageManager()) {
                                    refresh()
                                } else {
                                    "没授权".i()
                                }
                            }
                    }
                } else {
                    //android10即以下
                    // 先在AndroidManifest的application中设置requestLegacyExternalStorage = false 关闭分区储存
                    //这样就可以通过申请写入权限来获取外置储存的读写权
                    JPermissions(this@FileHomeActivity)
                        .success {
                            refresh()
                        }
                        .failure { _, _, _ ->
                            "没授权".i()
                        }.start(
                            arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        )
                }
                //相册
                setOpenAlbum {
                    startActivity(Intent(this@FileHomeActivity, FileImageActivity::class.java))
                }
            }
        test()
    }

    private fun test() {

        val storageManager = getSystemService(Context.STORAGE_SERVICE) as StorageManager
        for (storageVolume in storageManager.storageVolumes) {
            Log.i("directory", storageVolume.directory?.absolutePath.orEmpty())
            Log.i("mediaStoreVolumeName", storageVolume.mediaStoreVolumeName.orEmpty())

        }
    }

}