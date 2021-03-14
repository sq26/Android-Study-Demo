package com.sq26.experience.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import com.sq26.experience.R
import com.sq26.experience.app.OkHttpUtil
import com.sq26.experience.databinding.ActivityNetworkBinding
import com.sq26.experience.util.Log
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import okhttp3.Request
import javax.inject.Inject

@AndroidEntryPoint
class NetworkActivity : AppCompatActivity() {
    private val networkViewModel: NetworkViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityNetworkBinding>(this, R.layout.activity_network)
            .apply {
                lifecycleOwner = this@NetworkActivity
                viewModel = networkViewModel
            }
//        Log.i("NetworkActivity")
//        var id: Int?
//        binding.download.setOnClickListener {
////            id = Download.add("http://192.168.0.100:8080/04.mp4")
////            val id = Download(this).add("http://192.168.8.210:8080/2.mp4")
//
////            Log.i("id:$id")
////            Download.progress(id!!) {
////                Log.i("当前${it.current},全部${it.all}")
////            }
////            Download(this).delete()
//        }
//        binding.viewDownload.setOnClickListener {
//            Thread {
//                val downloadDao = DownloadDatabase.getInstance(this).getDownloadDao()
//                downloadDao.getAllDownloadList().observeForever {
//                    for (item in it)
//                        Log.i("download", item.toString())
//                }
//
//            }.start()
//        }
//        binding.viewDownloadSlice.setOnClickListener {
//            val downloadSliceDao =
//                DownloadDatabase.getInstance(applicationContext).getDownloadSliceDao()
//            for (item in downloadSliceDao.getAllDownloadSliceList())
//                Log.i("downloadSlice", item.toString())
//        }

    }
}
@HiltViewModel
class NetworkViewModel @Inject constructor(
//    @ActivityContext context: Context
) : ViewModel() {

    var content = ""

    fun get() {
        val response = OkHttpUtil.getInstance().newCall(
            Request.Builder().url("https://videoshfcx.tianqi.cn/outdata/other2020/tqwy_tylist")
                .build()
        ).execute().body?.string() ?: ""
        Log.i(response)
    }

    fun post() {

    }
}
