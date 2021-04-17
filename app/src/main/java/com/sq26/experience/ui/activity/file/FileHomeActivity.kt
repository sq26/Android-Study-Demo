package com.sq26.experience.ui.activity.file

import android.content.Intent
import android.os.Bundle
import android.text.format.Formatter
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonListAdapter
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.data.FileRoot
import com.sq26.experience.data.FileRootDao
import com.sq26.experience.databinding.ActivityFileHomeBinding
import com.sq26.experience.databinding.ItemFileHoemRootBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FileHomeActivity : AppCompatActivity() {

    @Inject
    lateinit var fileRootDao: FileRootDao
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
                    fileRootDao.insert(
                        FileRoot(
                            //获取分区名称
                            it.name.toString(),
                            //总大小
                            Formatter.formatFileSize(this@FileHomeActivity, it.length()),
                            //获取uri
                            uri.toString(),
                            //获取是否有读权限
                            it.canRead(),
                            //获取是否有写权限
                            it.canWrite()
                        )
                    )
                }
            }
        }
    private lateinit var adapter: CommonListAdapter<FileRoot>
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
                        request.launch(null)
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
                                            Intent(
                                                this@FileHomeActivity,
                                                FileOperateActivity::class.java
                                            )
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
                        this@FileHomeActivity,
                        DividerItemDecoration.VERTICAL
                    )
                )
                rootFileRecyclerView.adapter = adapter

                fileRootDao.queryAll().asLiveData().observe(this@FileHomeActivity) {
                    adapter.submitList(it)
                }

                setOpenAlbum {
                    startActivity(Intent(this@FileHomeActivity, FileImageActivity::class.java))
                }
            }

        initDate()
    }

    private fun initDate() {
        lifecycleScope.launch(Dispatchers.IO) {
            //type值是获取的文件夹的名字,null指应用文件的根目录,/storage/emulated/0/Android/data/com.sq26.experience/files
            val files = getExternalFilesDirs(null)
            //遍历分区
            for (file in files) {
                val documentFile = DocumentFile.fromFile(file)
                fileRootDao.insert(
                    FileRoot(
                        //获取分区名称
                        documentFile.name.toString(),
                        //总大小
                        Formatter.formatFileSize(this@FileHomeActivity, documentFile.length()),
                        //获取uri
                        documentFile.uri.toString(),
                        //获取是否有读权限
                        documentFile.canRead(),
                        //获取是否有写权限
                        documentFile.canWrite(),
                    )
                )
            }
        }
    }

}


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