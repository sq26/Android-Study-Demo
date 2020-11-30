package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sq26.experience.entity.JsonArrayViewMode
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.sq26.experience.databinding.ActivityViewModeBinding

class ViewModeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewModeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewModeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.text.text = "hello world"
        //创建ViewMode的使用方法
        //ViewMode会随着activity和fragment的结束而结束
        //ViewMode中不允许存在activity,fragment和View的引用,以防止生命周期结束,内存无法释放
        //需要使用context时使用AndroidViewModelFactory创建一个带有Application的ViewMode(ViewMode要继承AndroidViewModel)
        //不需要就用NewInstanceFactory创建无参的ViewMode
        //ViewMode用于纯粹的数据管理和数据处理
        //配合liveDate做同步或异步的数据刷新
        //创建视图数据模板
        val jsonArrayViewMode = ViewModelProvider(this, AndroidViewModelFactory(application)).get(JsonArrayViewMode::class.java)
        //在旋转屏幕时会调用onCreate,这里不会重新创建数据,但内部的观察者监听的回调会被重新调用
        jsonArrayViewMode.jsonArrayLiveData.observe(this, { objects ->
            Log.i("1", "1")
            //设置数据和恢复数据
            binding.text.text = objects.toJSONString()
        })
    }
}