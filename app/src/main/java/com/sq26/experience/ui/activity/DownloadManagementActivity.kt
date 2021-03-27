package com.sq26.experience.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.sq26.experience.databinding.ActivityDownloadManagementBinding

class DownloadManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDownloadManagementBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "下载管理器"
        setSupportActionBar(binding.toolbar)

//        downloadRecyclerView.adapter = object : RecyclerView.Adapter() {
//
//        }


    }
}

//class view(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//}