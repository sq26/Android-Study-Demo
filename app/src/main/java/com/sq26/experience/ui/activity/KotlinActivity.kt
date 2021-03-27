package com.sq26.experience.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sq26.experience.databinding.ActivityKotlinBinding
import com.sq26.experience.util.Log
import com.squareup.moshi.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class KotlinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKotlinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKotlinBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.text.text = "moshi"

        binding.text.setOnClickListener {
            val json =
                "{\"name\":null,\"address\":{\"city\":\"北京\",\"country\":\"中国\"},\"domain_list\":[{\"name\":\"ICP备案查询\",\"url\":\"https://icp.sojson.com\"},{\"name\":\"JSON在线解析\",\"url\":\"https://www.sojson.com\"},{\"name\":\"房贷计算器\",\"url\":\"https://fang.sojson.com\"}]}"

            val moshi = Moshi.Builder()
                .add(StringAdapter())
                .build()
            val jsonData = moshi.adapter(JsonData::class.java).fromJson(json)

            Log.i(jsonData.toString())
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

@JsonClass(generateAdapter = true)
data class JsonData(
    val name: String,
    val url: String="",
    val address: String,
    val domain_list: List<Domain>
)

@JsonClass(generateAdapter = true)
data class Address(
    val city: String,
    val country: String
)

@JsonClass(generateAdapter = true)
data class Domain(
    val name: String,
    val url: String
)


class StringAdapter : JsonAdapter<String>() {
    @FromJson
    override fun fromJson(reader: JsonReader): String {
        Log.i(reader)
        //判断是空类型
        if (reader.peek() == JsonReader.Token.NULL) {
            //跳过null类型
            reader.nextNull<Unit>()
            //返回空字符
            return ""
        }
        //判断是集合就直接取源json赋值
        if (reader.peek() == JsonReader.Token.BEGIN_OBJECT ||
            reader.peek() == JsonReader.Token.BEGIN_ARRAY
        ) {
            return reader.readJsonValue().toString()
        }
        //正常返回原本的String对象
        return reader.nextString()
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: String?) {
        writer.value(value)
    }
}




