package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.sq26.experience.BR
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityDataStoreBinding
import com.sq26.experience.util.Log
import com.sq26.experience.viewmodel.ObservableViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class DataStoreActivity : AppCompatActivity() {
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    // 推荐写法是直接定义在context顶层
//    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val dataStore: DataStore<Preferences> by preferencesDataStore("settings")

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
//                            Log.i(dataStoreViewModel.text, "save")
//                            it[text] = dataStoreViewModel.text
                            it[array] = mutableSetOf("123", "321")
                        }
                    }
                }

                setLoad {
                    //读取必须在flow中通过liveData的监听异步获取
                    dataStore.data.map {
                        it[text] ?: ""
                    }.asLiveData().observe(this@DataStoreActivity) {
//                        dataStoreViewModel.text = it
//                        Log.i(dataStoreViewModel.text, "load")
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
                    }

                }
            }
    }
}

@HiltViewModel
class DataStoreViewModel @Inject constructor() : ObservableViewModel() {


}
