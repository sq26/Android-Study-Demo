package com.sq26.experience.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import com.sq26.experience.databinding.ActivityNetworkBinding
import com.sq26.experience.util.network.download.Download
import com.sq26.experience.util.network.download.DownloadDatabase

class NetworkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNetworkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Logger.i("NetworkActivity")
        var id: Int?
        binding.download.setOnClickListener {
            id = Download(this).add("http://192.168.0.100:8080/04.mp4")
//            val id = Download(this).add("http://192.168.8.210:8080/2.mp4")

            Logger.i("id:$id")
            Download(this).progress(id!!) {
                Logger.i("当前${it.current},全部${it.all}")
            }
//            Download(this).delete()
        }
        binding.viewDownload.setOnClickListener {
            Thread {
                val downloadDao = DownloadDatabase.getInstance(this).getDownloadDao()
                downloadDao.getAllDownloadList().observeForever {
                    for (item in it)
                        Log.i("download", item.toString())
                }

            }.start()
        }
        binding.viewDownloadSlice.setOnClickListener {
            val downloadSliceDao = DownloadDatabase.getInstance(applicationContext).getDownloadSliceDao()
            for (item in downloadSliceDao.getAllDownloadSliceList())
                Log.i("downloadSlice", item.toString())
        }

    }
}
