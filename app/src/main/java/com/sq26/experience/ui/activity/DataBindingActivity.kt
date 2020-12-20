package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityDataBindingBinding
import com.sq26.experience.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.*

class DataBindingActivity : AppCompatActivity() {
    private val viewmodel: DataBindingViewModel by viewModels { DataBindingVMFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityDataBindingBinding>(
            this,
            R.layout.activity_data_binding
        )
        //绑定生命周期
        binding.lifecycleOwner =this
        //绑定视图模板
        binding.viewmodel = viewmodel

    }
}

object DataBindingVMFactory : ViewModelProvider.Factory {
    private val dataSource = DefaultDataSource(Dispatchers.IO)
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DataBindingViewModel(dataSource) as T
    }

}

class DefaultDataSource(private val ioDispatcher: CoroutineDispatcher) {

    fun getCurrentTime(): LiveData<Long> = liveData {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1000)
        }
    }

    // 每2秒公开一次不断变化的天气状况的LiveData。
    private val weatherConditions = listOf("Sunny", "Cloudy", "Rainy", "Stormy", "Snowy")
    fun fetchWeather(): LiveData<String> = liveData {
        var counter = 0
        while (true) {
            counter++
            delay(2000)

            emit(weatherConditions[counter % weatherConditions.size])
        }
    }

    // Cache of a data point that is exposed to VM
    private val _cachedData = MutableLiveData("This is old data")
    val cachedData: LiveData<String> = _cachedData

    //在需要刷新缓存时调用。 必须从协程调用。
    suspend fun fetchNewData() {
        // 主线程
        withContext(Dispatchers.Main) {
            _cachedData.value = "Fetching new data..."
            _cachedData.value = simulateNetworkDataFetch()
        }
    }

    // 在后台获取新数据。 必须从协程调用，以便正确确定范围。
    private var counter = 0

    // 使用ioDispatcher是因为该函数模拟了长时间且昂贵的操作。
    private suspend fun simulateNetworkDataFetch(): String = withContext(ioDispatcher) {
        delay(3000)
        counter++
        "New data from request #$counter"
    }
}

class DataBindingViewModel(private val dataSource: DefaultDataSource) : ViewModel() {

    companion object {
        // 实际应用会在结果类型上使用包装器来处理此问题。
        const val LOADING_STRING = "Loading..."
    }

    // 函数返回的LiveData，该函数返回使用liveData构建器生成的LiveData
    val currentTime = dataSource.getCurrentTime()

    fun onRefresh(){
        Log.i("点了一下")
    }

    // 协程内部的转换
    val currentTimeTransformed = currentTime.switchMap {
        // timeStampToTime是一个暂停函数，因此我们需要从协程调用它。
        liveData { emit(timeStampToTime(it)) }
    }

    // 在后台线程中模拟长时间运行的计算
    private suspend fun timeStampToTime(timestamp: Long): String {
        delay(500)  // 模拟长时间运行
        val date = Date(timestamp)
        return date.toString()
    }
}
