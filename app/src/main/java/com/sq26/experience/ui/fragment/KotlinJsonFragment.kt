package com.sq26.experience.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.sq26.experience.databinding.FragmentKotlinJsonBinding
import com.sq26.experience.util.i
import com.squareup.moshi.*
import kotlin.Exception
import kotlin.math.round

class KotlinJsonFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentKotlinJsonBinding.inflate(inflater, container, false).apply {
            //包含有所有类型的json(空值,字符串,数值,布尔,对象,数组)
            val jsonString =
                "{\"name\":null,\"url\":\"http://www.bejson.com\",\"page\":88,\"isNonProfit\":true,\"address\":{\"street\":\"科技园路.\",\"city\":null,\"country\":\"中国\"},\"links\":[{\"name\":\"Google\",\"url\":\"http://www.google.com\"},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\"},{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"}]}"

            val jsonString2 =
                "[{\"name\":\"Google\",\"url\":\"http://www.google.com\"},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\"},{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"}]"

            /**
             * 将json转换为kotlin data 类的的3大问题(data类所有对象都是空安全)
             * 1.json缺少key值导致值为null,设置默认值可解决
             * 2.json的value值是null,null值会覆盖默认值
             * 3.json的value类型和data类的对象不匹配,直接类型异常
             * 将json转换为kotlin data 类的并且所有对象都是空安全的解决方法
             * 1.data的所有对象最好都设置为string类型,并且所有对象都给定默认值
             * 2.设置自定义解析,这样json的值是null时,同过自定义解析将null值替换为空默认值,null值就不会被设置到对象了
             * 3.都设置成string,是因为是为了类型转换,所有类型的数据都可以无损转成string,
             * string也可以便捷的转成其他格式,object和array类型可以把原始json字符存进string,在进行二次json序列化
             */
            //gson
            var l1 = System.currentTimeMillis()
            val gson = GsonBuilder().serializeNulls()
                .registerTypeAdapterFactory(GsonDefaultAdapterFactory())
                .create()
            (System.currentTimeMillis() - l1).i("l11")//18
            val c1 = System.currentTimeMillis()
            l1 = System.currentTimeMillis()
            //这个是完整的json解析
            text1.text = gson.fromJson(jsonString, TestData::class.java).toString()
            (System.currentTimeMillis() - l1).i("l12")//14
            //解析出错返回新的对象
            val s: String? = null

            l1 = System.currentTimeMillis()
            text2.text = gson.fromJson<List<LinksItem>>(
                jsonString2,
                object : TypeToken<List<LinksItem>>() {}.type
            ).toString()
            (System.currentTimeMillis() - l1).i("l13")//2
            (System.currentTimeMillis() - c1).i("c1")//17

            val NULLgson = gson.fromJsonOrEmptyList<LinksItem>(jsonString2)
            Log.i("NULLgson", NULLgson.toString())

            //moshi
            var l2 = System.currentTimeMillis()
            val moshi: Moshi = Moshi.Builder()
//                .addLast(KotlinJsonAdapterFactory())
                .add(StringNotNUllAdapter())
                .build()
            (System.currentTimeMillis() - l2).i("l21")//10

//            l2 = System.currentTimeMillis()
//            val adapter: JsonAdapter<TestData> = moshi.adapter(TestData::class.java)
//            (System.currentTimeMillis() - l2).i("l22")//480

            val c2 = System.currentTimeMillis()
            l2 = System.currentTimeMillis()
            val adapter = TestDataJsonAdapter(moshi)
            (System.currentTimeMillis() - l2).i("l22")//21

            l2 = System.currentTimeMillis()
            text3.text = adapter.fromJson(jsonString).toString()
            (System.currentTimeMillis() - l2).i("l23")//10

            l2 = System.currentTimeMillis()
            val adapter2 = moshi.adapter<List<LinksItem>>(
                Types.newParameterizedType(
                    List::class.java,
                    LinksItem::class.java
                )
            )
            text4.text = adapter2.fromJson(jsonString2).toString()
            (System.currentTimeMillis() - l2).i("l24")//1
            (System.currentTimeMillis() - c2).i("c2")//33

            //解析出错后返回新对象
            val NULLmoshi = adapter2.fromJsonListEmpty(jsonString2)
            Log.i("NULLmoshi", NULLmoshi.toString())

            //moshi需要构建适配器,moshi的适配器构建耗时巨大,使用moshi-kotlin-codegen后效果有巨大提升
            //moshi的解析比gson快,但moshi需要构建适配器比gson繁琐,moshi的适配器构建耗时大,在Android开发中不可能把moshi的适配器设置成常量,
            //android内存是稀有资源,让cpu负载高一些可以接受,内存这种用完就该立刻回收
            //在Android环境下,综合性能和内存均衡考虑,应该用gson,要更好一些
        }.root
    }


}

//Gsom解析出错后返回不为null的新对象
fun <T> Gson.fromJsonOrEmpty(json: String?, c: Class<T>): T {
    return if (json.isNullOrEmpty())
        c.newInstance()
    else
        try {
            fromJson(json, c)
        } catch (e: Exception) {
            e.printStackTrace()
            c.newInstance()
        }
}

fun <T> Gson.fromJsonOrEmptyList(json: String?): List<T> {
    return if (json.isNullOrEmpty()) {
        listOf()
    } else {
        try {
            fromJson(
                json,
                object : TypeToken<List<T>>() {}.type
            )
        } catch (e: Exception) {
            e.printStackTrace()
            listOf()
        }
    }
}

//Moshi 对象解析出错后返回不为null的新对象
fun <T> JsonAdapter<T>.fromJsonEmpty(json: String?, c: T): T {
    return if (json.isNullOrEmpty())
        c
    else
        try {
            fromJson(json) ?: c
        } catch (e: Exception) {
            e.printStackTrace()
            c
        }
}

//Moshi 数组解析出错后返回空列表
fun <T> JsonAdapter<List<T>>.fromJsonListEmpty(json: String?): List<T> {
    return if (json.isNullOrEmpty())
        listOf()
    else
        try {
            fromJson(json) ?: listOf()
        } catch (e: Exception) {
            e.printStackTrace()
            listOf()
        }
}

//gson自定义适配器工厂
class GsonDefaultAdapterFactory : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
//        type.type.toString().i("1")
        return when (type.type) {
            //string类型的自定义类型转化
            String::class.java -> gsonStringTypeAdapter(gson)
            else -> null
        }
    }

    //处理object
    private fun handleOBJECT(reader: JsonReader): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        reader.beginObject()
        while (reader.hasNext()) {
            map[reader.nextName()] = when (reader.peek()) {
                JsonToken.NULL -> {
                    reader.nextNull()
                    null
                }
                JsonToken.STRING -> {
                    reader.nextString()
                }
                JsonToken.NUMBER -> {
                    val d = reader.nextDouble()
                    if (d == round(d)) d.toLong() else d
                }
                JsonToken.BOOLEAN -> {
                    reader.nextBoolean()
                }
                JsonToken.BEGIN_OBJECT -> {
                    handleOBJECT(reader)
                }
                JsonToken.BEGIN_ARRAY -> {
                    handleARRAY(reader)
                }
                else -> null
            }
        }
        reader.endObject()
        return map
    }

    //处理array
    private fun handleARRAY(reader: JsonReader): List<Any?> {
        val list = mutableListOf<Any?>()
        reader.beginArray()
        while (reader.hasNext()) {
            list.add(
                when (reader.peek()) {
                    JsonToken.NULL -> {
                        reader.nextNull()
                        null
                    }
                    JsonToken.STRING -> {
                        reader.nextString()
                    }
                    JsonToken.NUMBER -> {
                        val d = reader.nextDouble()
                        if (d == round(d)) d.toLong() else d
                    }
                    JsonToken.BOOLEAN -> {
                        reader.nextBoolean()
                    }
                    JsonToken.BEGIN_OBJECT -> {
                        handleOBJECT(reader)
                    }
                    JsonToken.BEGIN_ARRAY -> {
                        handleARRAY(reader)
                    }
                    else -> null
                }
            )
        }
        reader.endArray()
        return list
    }

    //string自定义适配器
    private fun <T> gsonStringTypeAdapter(gson: Gson): TypeAdapter<T> {
        return object : TypeAdapter<String>() {
            //写出
            override fun write(out: JsonWriter?, value: String?) {
                out?.value(value)
            }

            //读入
            override fun read(reader: JsonReader): String {
                //判断读到的json对象类型,转成string返回,不需要判断数值类型,gson的nextString已经做好了处理
//                reader.peek().name.i()
                return when (reader.peek()) {
                    JsonToken.NULL -> {
                        reader.nextNull()
                        ""
                    }
                    JsonToken.BOOLEAN -> reader.nextBoolean().toString()
                    JsonToken.BEGIN_OBJECT -> gson.toJson(handleOBJECT(reader))
                    JsonToken.BEGIN_ARRAY -> gson.toJson(handleARRAY(reader))
                    else -> reader.nextString()
                }
            }
        } as TypeAdapter<T>
    }
}


//string null处理adapter
class StringNotNUllAdapter : JsonAdapter<String>() {
    @FromJson
    override fun fromJson(reader: com.squareup.moshi.JsonReader): String {
        return when (reader.peek()) {
            com.squareup.moshi.JsonReader.Token.NULL -> {
                reader.nextNull<Any>()
                ""
            }
            com.squareup.moshi.JsonReader.Token.NUMBER, com.squareup.moshi.JsonReader.Token.BEGIN_ARRAY, com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT -> reader.nextSource()
                .readUtf8()
            else -> reader.readJsonValue().toString()
        }
    }

    @ToJson
    override fun toJson(writer: com.squareup.moshi.JsonWriter, value: String?) {
        writer.value(value)
    }
}

@JsonClass(generateAdapter = true)
data class TestData(
    val name: String = "",
    val url: String = "",
    val page: String = "",
    val isNonProfit: String = "",
    val address: String = "",
    val links: String = "",
)

data class TestData1(
    val name: String = "",
    val url: String = "",
    val page: Int = 0,
    val isNonProfit: Boolean = false,
)

data class Address(
    val street: String = "",
    val city: Int = 0,
    val country: Boolean = false,
    val a1: String = "",
    val a2: TestData1 = TestData1(),
    val array: List<String> = listOf()
)

@JsonClass(generateAdapter = true)
data class LinksItem(
    val name: String = "",
    val url: String = "",
)
