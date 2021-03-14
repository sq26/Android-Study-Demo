package com.sq26.experience.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sq26.experience.databinding.ItemAppInfoBinding
import com.sq26.experience.ui.activity.AppInfo
import java.io.File
import java.io.FileInputStream

class AppManagementAdapter :
    ListAdapter<AppInfo, AppManagementAdapter.ViewHolder>(AppManagementDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemAppInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemAppInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setOnClick { view ->
                MaterialAlertDialogBuilder(view.context)
                    .setItems(arrayOf("把apk保存在指定位置")) { _, i ->
                        when (i) {
                            0 -> {
                                (view.context as AppCompatActivity).registerForActivityResult(
                                    ActivityResultContracts.StartActivityForResult()
                                ) {
                                    it?.data?.data?.apply {
                                        val outputStream =
                                            (view.context as AppCompatActivity).contentResolver.openOutputStream(
                                                this
                                            )
                                        val fileInputStream =
                                            FileInputStream(File(binding.appInfo!!.packageInfo.applicationInfo.sourceDir))
                                        val bytes = ByteArray(4096)
                                        var index: Int
                                        while ((fileInputStream.read(bytes).also { i ->
                                                index = i
                                            }) != -1) {
                                            outputStream?.write(bytes, 0, index)
                                            outputStream?.flush()
                                        }
                                        outputStream?.close()
                                        fileInputStream.close()
                                        Toast.makeText(view.context, "保存完毕!", Toast.LENGTH_LONG).show()
                                    }
                                }.launch(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                    type = "application/apk"
                                    putExtra(
                                        Intent.EXTRA_TITLE,
                                        binding.appInfo!!.packageInfo.applicationInfo.loadLabel(
                                            view.context.packageManager
                                        ).toString() + ".apk"
                                    )
                                })
                            }
                        }
                    }
                    .show()
            }
        }

        fun bind(appInfo: AppInfo) {
            binding.icon.setImageDrawable(appInfo.icon)
            binding.appInfo = appInfo
        }
    }

    class AppManagementDiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.text == newItem.text
        }

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem == newItem
        }
    }
}