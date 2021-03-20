package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.*
import com.sq26.experience.BR
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityDataBindingBinding
import com.sq26.experience.util.Log
import com.sq26.experience.util.kotlin.toast
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DataBindingActivity : AppCompatActivity() {
    private val viewModel: DataBindingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //创建数据绑定
        DataBindingUtil.setContentView<ActivityDataBindingBinding>(
            this,
            R.layout.activity_data_binding
        ).apply {
            //绑定生命周期(不知道什么原理,绑定生命周期写在apply方法中才生效,才可以自动刷新)
            lifecycleOwner = this@DataBindingActivity
            //绑定视图模板
            viewmodel = viewModel

            setOnClick {
                toast("DataBinding绑定点击事件")
            }
        }
    }
}
@HiltViewModel
class DataBindingViewModel @Inject constructor(
//    @ActivityContext private val context: Context
) : ViewModel(), Observable {
    private val callbacks = PropertyChangeRegistry()
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }
    //Bindable注解给currentTime打上标识,可用于之后的手动数据更新
    @Bindable
    var currentTime = System.currentTimeMillis()

    @Bindable
    var currentTime2 = System.currentTimeMillis()

    //更新currentTime
    fun updateCurrentTime() {
        //给currentTime和currentTime2都设置最新的时间戳
        currentTime = System.currentTimeMillis()
        currentTime2 = System.currentTimeMillis()
        //只刷新currentTime,这样就可以只用刷新currentTime的数据而不刷新currentTime2
        callbacks.notifyChange(this, BR.currentTime)
    }

    fun onClick() {
//        AlertDialog.Builder(context)
//            .setMessage("viewModel绑定点击事件")
//            .show()
//        Toast.makeText(context, "viewModel绑定点击事件", Toast.LENGTH_SHORT).show()
    }

    var checked = false

    fun onTest() {
        Log.i(checked.toString(), "checked")
        editText.postValue("123")
    }

    var editText = MutableLiveData<String>()


    // 协程内部的转换
//    val currentTimeTransformed = currentTime.switchMap {
//        // timeStampToTime是一个暂停函数，因此我们需要从协程调用它。
//        liveData { emit(timeStampToTime(it)) }
//    }

    // 在后台线程中模拟长时间运行的计算
//    private suspend fun timeStampToTime(timestamp: Long): String {
//        delay(500)  // 模拟长时间运行
//        val date = Date(timestamp)
//        return date.toString()
//    }

}
