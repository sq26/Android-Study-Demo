package com.sq26.experience.ui.fragment.file

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import com.sq26.experience.adapter.CommonListAdapter
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.adapter.MarginItemDecoration
import com.sq26.experience.databinding.FragmentFileHomeBinding
import com.sq26.experience.databinding.ItemFileHomeStorageBinding
import com.sq26.experience.util.kotlin.getFileSizeStr
import com.sq26.experience.util.kotlin.toast
import com.sq26.experience.util.permissions.JPermissions

class FileHomeFragment : Fragment() {
    private val requestFilePermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    initDate()
                } else {
                    requireContext().toast("未获取权限")
                }
            }
        }

    private lateinit var adapter: CommonListAdapter<StorageInfoEntity>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFileHomeBinding.inflate(inflater).apply {
            adapter =
                object : CommonListAdapter<StorageInfoEntity>(StorageInfoEntityDiffItem()) {
                    override fun createView(
                        parent: ViewGroup,
                        viewType: Int
                    ): CommonListViewHolder<*> {
                        return object : CommonListViewHolder<ItemFileHomeStorageBinding>(
                            ItemFileHomeStorageBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
                        ) {

                            init {
                                v.root.setOnClickListener {

                                }
                            }

                            override fun bind(position: Int) {
                                v.item = getItem(position)
                            }
                        }
                    }

                }
            recyclerView.addItemDecoration(MarginItemDecoration(requireContext()))
            recyclerView.adapter = adapter
            initPermissions()
        }.root
    }

    private fun initPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //判断是由以获得权限
            if (Environment.isExternalStorageManager()) {
                initDate()
            } else {
                //去申请(这里需要自己告知用户申请权限的目地)
                requestFilePermission.launch(
                    Intent(
                        Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    )
                )
            }
        } else {
            JPermissions(requireActivity())
                .success {
                    initDate()
                }
                .failure { _, _, noPrompt ->
                    if (noPrompt.isNotEmpty()) {
                        AlertDialog.Builder(requireActivity()).setTitle("申请失败")
                            .setMessage("因为多次拒绝,需要去系统设置页面设置权限")
                            .setPositiveButton("确定", null)
                            .setNegativeButton("打开权限设置页面") { _, _ ->
                                JPermissions.openSettings(requireActivity())
                            }
                            .show()
                    } else {
                        requireContext().toast("未获取权限")
                    }
                }
                .start(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
        }
    }

    private fun initDate() {
        val storageManager =
            requireActivity().getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val list = mutableListOf<StorageInfoEntity>()
        for (storageVolume in storageManager.storageVolumes) {
            list.add(
                StorageInfoEntity(
                    storageVolume.mediaStoreVolumeName.orEmpty(),
                    storageVolume.directory?.absolutePath.orEmpty(),
                    storageVolume.isPrimary,
                    storageVolume.isRemovable
                )
            )

        }
        adapter.submitList(list)
    }

}

data class StorageInfoEntity(
    //储存卷名称
    val name: String,
    //路径
    val path: String,
    //是否主储存设备
    val isPrimary: Boolean,
    //是否可移动
    val isRemovable: Boolean,
    //可用大小
    var freeBytes: Long = 0,
    //总大小
    var totalBytes: Long = 0,
) {
    private fun freeBytesText() = freeBytes.getFileSizeStr()
    private fun totalBytesText() = totalBytes.getFileSizeStr()

    fun spatialDescription() = "可用:${freeBytesText()},全部:${totalBytesText()}"
    fun categoryDescription() =
        "是否主储存:${if (isPrimary) "是" else "否"},是否可移动储存:${if (isRemovable) "是" else "否"}"

    init {
        val statFs = StatFs(path)
        freeBytes = statFs.freeBytes
        totalBytes = statFs.totalBytes
    }
}

class StorageInfoEntityDiffItem : DiffUtil.ItemCallback<StorageInfoEntity>() {
    override fun areItemsTheSame(oldItem: StorageInfoEntity, newItem: StorageInfoEntity): Boolean {
        return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(
        oldItem: StorageInfoEntity,
        newItem: StorageInfoEntity
    ): Boolean {
        return oldItem == newItem
    }

}