package com.sq26.experience.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonListAdapter
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.data.DownloadEntity
import com.sq26.experience.data.DownloadEntityDiffCallback
import com.sq26.experience.databinding.ActivityDownloadManagementBinding
import com.sq26.experience.databinding.ItemDownloadBinding
import com.sq26.experience.util.Log
import com.sq26.experience.util.i
import com.sq26.experience.util.network.download.Download
import com.sq26.experience.util.network.download.DownloadService
import com.sq26.experience.util.network.download.DownloadStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class DownloadManagementActivity : AppCompatActivity() {

    @Inject
    lateinit var download: Download

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityDownloadManagementBinding>(
            this,
            R.layout.activity_download_management
        ).apply {
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            toolbar.menu.add("添加").apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                setOnMenuItemClickListener {

                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {

                            download.add("http://192.168.8.174:8080/18.mp4")
//                            download.add("http://w4.wallls.com/uploads/original/201711/25/wallls.com_169729.jpg")
                        }
                    }
                    true
                }
            }
            downloadRecyclerView.addItemDecoration(
                DividerItemDecoration(
                    this@DownloadManagementActivity,
                    DividerItemDecoration.VERTICAL
                )
            )

            val adapter = object : CommonListAdapter<DownloadEntity>(DownloadEntityDiffCallback) {
                override fun createView(parent: ViewGroup, viewType: Int): CommonListViewHolder<*> {
                    return object : CommonListViewHolder<ItemDownloadBinding>(
                        ItemDownloadBinding.inflate(
                            LayoutInflater.from(parent.context)
                        )
                    ) {
                        override fun bind(position: Int) {
                            val item = getItem(position)
                            v.item = item
                            executePendingBindings()
                            download.getDownloadFlow(item.id)
                                .observe(this@DownloadManagementActivity) {
                                    Log.i(it, "item")
                                    v.alreadySize.text = it.alreadySize.toString()
                                    v.size.text = it.size.toString()
                                    v.status.text = it.status.toString()
                                    when (it.status) {
                                        DownloadStatus.DOWNLOADING -> {
                                            v.button.text = "暂停"
                                            v.button.isVisible = true
                                            v.button.setOnClickListener {
                                                download.pause(item.id)
                                            }
                                        }
                                        DownloadStatus.PAUSE -> {
                                            v.button.text = "继续"
                                            v.button.isVisible = true
                                            v.button.setOnClickListener {
                                                download.continuance(item.id)
                                            }
                                        }
                                        else ->
                                            v.button.isVisible = false
                                    }
                                    if (it.status == DownloadStatus.ERROR || it.status == DownloadStatus.COMPLETE) {
                                        v.button2.text = "删除"
                                        v.button2.setOnClickListener {
                                            MaterialAlertDialogBuilder(this@DownloadManagementActivity)
                                                .setMessage("是否保留文件")
                                                .setPositiveButton("是") { _, _ ->
                                                    download.delete(item.id)
                                                }
                                                .setNegativeButton("删除") { _, _ ->
                                                    download.delete(item.id, false)
                                                }
                                                .show()

                                        }
                                    } else {
                                        v.button2.text = "取消"
                                        v.button2.setOnClickListener {
                                            download.delete(item.id, false)
                                        }
                                    }
                                }
                        }

                    }
                }

            }

            downloadRecyclerView.adapter = adapter

            download.queryAllFlow().observe(this@DownloadManagementActivity) {
                Log.i(it)
                adapter.submitList(it)
            }


            delete.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    download.deleteAll()
                }
            }

            closeServer.setOnClickListener {
                stopService(Intent(this@DownloadManagementActivity, DownloadService::class.java))
            }
        }

    }
}