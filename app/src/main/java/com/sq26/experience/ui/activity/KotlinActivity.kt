package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sq26.experience.databinding.ActivityKotlinBinding
import com.sq26.experience.util.Log
import com.sq26.experience.util.log
import com.sq26.experience.util.network.download.Download
import kotlinx.coroutines.*

class KotlinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKotlinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKotlinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.text.text = "点击下载图片"
        binding.text1.text = "重试"
        binding.text2.text = "删除"
        var id = 0
        binding.text1.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                Download.retries(id)
                Download.progress(id) {
                    com.sq26.experience.util.Log.i("progress:$it")
                }
            }

        }
        binding.text.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                id = withContext(Dispatchers.IO) {
                    Download.add("http://w4.wallls.com/uploads/original/201808/05/wallls.com_196057.jpg")
                }
                com.sq26.experience.util.Log.i("id:${id}")
                Download.progress(id) {
                    com.sq26.experience.util.Log.i("progress:$it")
                }

            }
        }
        binding.text2.setOnClickListener {
            "string".log()
            Log.i("i")
            Log.d("d")
            Log.w("w")
            Log.e("e")
            Log.v("v")
        }

    }

}