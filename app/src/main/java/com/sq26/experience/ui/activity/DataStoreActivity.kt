package com.sq26.experience.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.sq26.experience.BR
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityDataStoreBinding
import com.sq26.experience.util.Log
import com.sq26.experience.viewmodel.ObservableViewModel
import com.sq26.proto.Demo
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@AndroidEntryPoint
class DataStoreActivity : AppCompatActivity() {
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    // 推荐写法是直接定义在context顶层
//    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val dataStore: DataStore<Preferences> by preferencesDataStore("settings")

    private val protoDataStore: DataStore<Demo.DemoItem> by dataStore(
        fileName = "demo.pb",
        serializer = DemoSerializer
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityDataStoreBinding>(this, R.layout.activity_data_store)
            .apply {
                lifecycleOwner = this@DataStoreActivity
                viewModel = dataStoreViewModel

                toolbar.setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
                //默认的dataStore只能使用set<string>集合
                val array = stringSetPreferencesKey("array")
                //以及基本数据类型
                val text = stringPreferencesKey("text")
                setSave {
                    //必须在携程中使用保存
                    lifecycleScope.launch {
                        dataStore.edit {
                            Log.i(dataStoreViewModel.text, "save")
                            it[text] = dataStoreViewModel.text
                            it[array] = mutableSetOf("123", "321")
                        }
                    }
                }

                setLoad {
                    //读取必须在flow中通过liveData的监听异步获取
                    dataStore.data.map {
                        it[text] ?: ""
                    }.asLiveData().observe(this@DataStoreActivity) {
                        dataStoreViewModel.text = it
                        Log.i(dataStoreViewModel.text, "load")
                    }

                    dataStore.data.map {
                        it[array] ?: setOf()
                    }.asLiveData().observe(this@DataStoreActivity) {
                        Log.i(it.toString(), "load")
                    }
                }


                setSave2 {
                    //runBlocking会堵塞当前线程,最好放在协程中使用
                    runBlocking(Dispatchers.IO) {
                        //更新protoDataStore中的数据
                        protoDataStore.updateData {
                            it.toBuilder()
                                .setPhoto(12)
                                .setText("ttt")
                                .build()
                        }
                    }

                }

                setLoad2 {
                    //同步的获取方式
//                    val demoItem = runBlocking {
//                        protoDataStore.data.first()
//                    }
//                    //异步获取
//                    protoDataStore.data.asLiveData().observe(this@DataStoreActivity){
//                        it
//                    }
                    //协程
                    lifecycleScope.launch {
                        //协程同步获取
//                        val demoItem = withContext(Dispatchers.IO) {
//                            protoDataStore.data.first()
//                        }
                        //协程异步获取
                        protoDataStore.data.collect {
                            Log.i(it.toString(), "load2")
                            Log.i(it.photo, "load21")
                        }
                    }

                }
            }
    }
}

@HiltViewModel
class DataStoreViewModel @Inject constructor() : ObservableViewModel() {

    @Bindable
    var text = ""
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.text)
            }
        }

}

object DemoSerializer : Serializer<Demo.DemoItem> {
    //初始化默认值
    override val defaultValue: Demo.DemoItem
        get() = Demo.DemoItem.getDefaultInstance()

    //读取格式化
    override suspend fun readFrom(input: InputStream): Demo.DemoItem {
        try {
            return Demo.DemoItem.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    //写入
    override suspend fun writeTo(t: Demo.DemoItem, output: OutputStream) {
        return t.writeTo(output)
    }


}