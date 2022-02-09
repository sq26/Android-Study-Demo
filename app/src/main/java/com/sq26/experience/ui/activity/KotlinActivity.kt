package com.sq26.experience.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityKotlinBinding
import com.sq26.experience.util.Log
//import com.squareup.moshi.*
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


fun ComponentActivity.setReceiver(fun1: () -> Unit) {
    val local = "$packageName.local"
    val localBroadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("接收到本地消息")
            fun1()
        }
    }

    val intentFilter = IntentFilter(local)
    var b = false
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            if (b)
                registerReceiver(localBroadcast, intentFilter)
            b = true
            Log.i("运行中")
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            unregisterReceiver(localBroadcast)
            Log.i("暂停中")
        }

    })
    registerReceiver(localBroadcast, intentFilter)
}

@AndroidEntryPoint
class KotlinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKotlinBinding
    private val viewModel: KotlinViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_kotlin)

        binding.text.text = "moshi"
        binding.text.setOnClickListener {
            val json =
                "{\"name\":null,\"address\":{\"city\":\"北京\",\"country\":\"中国\"},\"domain_list\":[{\"name\":\"ICP备案查询\",\"url\":\"https://icp.sojson.com\"},{\"name\":\"JSON在线解析\",\"url\":\"https://www.sojson.com\"},{\"name\":\"房贷计算器\",\"url\":\"https://fang.sojson.com\"}]}"

//            val moshi = Moshi.Builder()
//                .add(StringAdapter())
//                .build()
//            val jsonData = moshi.adapter(JsonData::class.java).fromJson(json)
//
//            Log.i(jsonData.toString())
        }

        setReceiver {
            //在这里做操作也可以把操作写死
            MaterialAlertDialogBuilder(this)
                .setMessage("成功")
                .show()

        }

        binding.text1.setOnClickListener {

            viewModel.start()

        }

//        var id = 0
//        binding.text1.setOnClickListener {
//            CoroutineScope(Dispatchers.Main).launch {
//                Download.retries(id)
//                Download.progress(id) {
//                    com.sq26.experience.util.Log.i("progress:$it")
//                }
//            }
//
//        }
//        binding.text.setOnClickListener {
//            CoroutineScope(Dispatchers.Main).launch {
//                id = withContext(Dispatchers.IO) {
//                    Download.add("http://w4.wallls.com/uploads/original/201808/05/wallls.com_196057.jpg")
//                }
//                com.sq26.experience.util.Log.i("id:${id}")
//                Download.progress(id) {
//                    com.sq26.experience.util.Log.i("progress:$it")
//                }
//
//            }
//        }
//        binding.text2.setOnClickListener {
//            "string".log()
//            Log.i("i")
//            Log.d("d")
//            Log.w("w")
//            Log.e("e")
//            Log.v("v")
//        }

    }

}

//@JsonClass(generateAdapter = true)
//data class JsonData(
//    val name: String,
//    val url: String = "",
//    val address: String,
//    val domain_list: List<Domain>
//)
//
//@JsonClass(generateAdapter = true)
//data class Address(
//    val city: String,
//    val country: String
//)
//
//@JsonClass(generateAdapter = true)
//data class Domain(
//    val name: String,
//    val url: String
//)


//class StringAdapter : JsonAdapter<String>() {
//    @FromJson
//    override fun fromJson(reader: JsonReader): String {
//        Log.i(reader)
//        //判断是空类型
//        if (reader.peek() == JsonReader.Token.NULL) {
//            //跳过null类型
//            reader.nextNull<Unit>()
//            //返回空字符
//            return ""
//        }
//        //判断是集合就直接取源json赋值
//        if (reader.peek() == JsonReader.Token.BEGIN_OBJECT ||
//            reader.peek() == JsonReader.Token.BEGIN_ARRAY
//        ) {
//            return reader.readJsonValue().toString()
//        }
//        //正常返回原本的String对象
//        return reader.nextString()
//    }
//
//    @ToJson
//    override fun toJson(writer: JsonWriter, value: String?) {
//        writer.value(value)
//    }
//}

@HiltViewModel
class KotlinViewModel @Inject constructor(
    private val requestClass: RequestClass
) : ViewModel() {
    fun start() = requestClass.requestError()
}

@Singleton
class RequestClass @Inject constructor(@ApplicationContext private val context: Context) {

    fun requestError() {
        context.sendBroadcast(Intent("${context.packageName}.local").apply {
            //指定包名
            setPackage(context.packageName)
        })
    }

}





