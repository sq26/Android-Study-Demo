package com.sq26.experience.viewmodel

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * 一个观察的视图模型，与数据绑定库一起使用。
 */
open class ObservableViewModel : ViewModel(), Observable {
    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    override fun addOnPropertyChangedCallback(
        callback: Observable.OnPropertyChangedCallback
    ) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(
        callback: Observable.OnPropertyChangedCallback
    ) {
        callbacks.remove(callback)
    }

    //通知观察者此实例的所有属性都已更改。
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     *通知观察者特定属性已更改。这场比赛的打气者
     *属性，该属性的更改应使用@Bindable注释进行标记
     *在BR类中生成一个字段，用作fieldId参数。
     * @param fieldId 为可绑定字段生成的BR id。
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }
}

/**
 * 提供activity基本能力的liveDate回调
 */
open class BaseViewModel : ObservableViewModel() {
    //关闭页面的调用
    val finish = MutableLiveData<FinishDate>()

    //显示toast的调用
    val toast = MutableLiveData<String>()
}

data class FinishDate(
    //设置是否有回调接受
    val boolean: Boolean,
    //设置是否有参数返回
    val intent: Intent = Intent()
)

/**
 * 给ComponentActivity添加
 * 快速设置回调的BaseViewModel工具方法
 */
fun ComponentActivity.setBaseViewModel(viewModel: BaseViewModel){
    //注册关闭页面的回调
    viewModel.finish.observe(this) {
        if (it.boolean)
            this.setResult(Activity.RESULT_OK, it.intent)
        this.finish()
    }
    //注册显示Toast的调用
    viewModel.toast.observe(this) {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }
}