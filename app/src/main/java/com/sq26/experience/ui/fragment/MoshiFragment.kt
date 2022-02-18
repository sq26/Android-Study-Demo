package com.sq26.experience.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sq26.experience.databinding.FragmentMoshiBinding
import com.sq26.experience.util.i
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MoshiFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentMoshiBinding.inflate(inflater, container, false).apply {
            val jsonString =
                "{\"name\":\"JSON中国\",\"url\":\"http://www.json.org.cn\",\"page\":88,\"isNonProfit\":true,\"address\":{\"street\":\"浙大路38号.\",\"city\":\"浙江杭州\",\"country\":\"中国\"},\"links\":[{\"name\":\"Google\",\"url\":\"http://www.google.com\"},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\"},{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"}]}"
            val jsonString2 =
                "{\"name\":\"JSON中国\",\"page\":88,\"isNonProfit\":true,\"address\":{\"street\":\"浙大路38号.\",\"city\":\"浙江杭州\",\"country\":\"中国\"},\"links\":[{\"name\":\"Google\",\"url\":\"http://www.google.com\"},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\"},{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"}]}"
            val jsonString3 =
                "{\"name\":\"JSON中国\",\"url\":null,\"page\":88.8,\"isNonProfit\":true,\"address\":{\"street\":\"浙大路38号.\",\"city\":\"浙江杭州\",\"country\":\"中国\"},\"links\":[{\"name\":\"Google\",\"url\":\"http://www.google.com\"},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\"},{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"}]}"
            val jsonString4 = "{\"name\":\"88\",\"url\":null,\"page\":88.8,\"isNonProfit\":true,\"address\":88,\"links\":[{\"name\":\"Google\",\"url\":\"http://www.google.com\"},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\"},{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"}]}"
            //由于未知原因目前只能为string类处理空值
            val moshi: Moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .add(StringNotNUllAdapter())
                .build()

//         val a=   moshi.adapter<List<TestData>>()
            val adapter: JsonAdapter<TestData> = moshi.adapter(TestData::class.java)

//            text1.text = adapter.fromJson(jsonString)?.toString()
//
//            text2.text = adapter.fromJson(jsonString2)?.toString()
//
//            text3.text = adapter.fromJson(jsonString3)?.toString()

            text4.text = adapter.fromJson(jsonString4)?.toString()

        }.root
    }
}

//string null处理adapter
class StringNotNUllAdapter : JsonAdapter<String>() {
    @FromJson
    override fun fromJson(reader: JsonReader): String {
        reader.peek().name.i("string")
        return when (reader.peek()) {
            JsonReader.Token.NULL -> {
                reader.nextNull<Any>()
                ""
            }
            JsonReader.Token.NUMBER, JsonReader.Token.BEGIN_ARRAY, JsonReader.Token.BEGIN_OBJECT -> reader.nextSource()
                .readUtf8()
            else -> reader.readJsonValue().toString()
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: String?) {
        writer.value(value)
    }
}
//只有string类型可以抵挡null值
data class TestData(
    val name: String,
    val url: String,
    val page: String,
    val isNonProfit: String,
    val address: String,
    val links: String
)

data class Address(
    val street: String,
    val city: String,
    val country: String,
)

data class LinksItem(
    val name: String,
    val url: String,
)
