package com.sq26.experience.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import butterknife.ButterKnife
import com.sq26.experience.databinding.ActivityLiveDataBinding
import com.sq26.experience.util.LifecycleThread

class LiveDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLiveDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ButterKnife.bind(this)
        //设置进度条最大值100
        binding.progressBar.max = 100
        //设置文本1默认值1
        binding.text1.text = "1"
        //设置文本2默认值2
        binding.text2.text = "2"
        //创建进度条数据源类型
        val LiveDataProgress = MutableLiveData<Int>()
        //设置进度条数据源监听
        LiveDataProgress.observe(this, { integer: Int -> binding.progressBar.progress = integer })
        //可以添加多个监听回调
        LiveDataProgress.observe(this, { integer: Int -> Log.i("1", integer.toString() + "") })
        //创建进度条缓冲条数据源类型
        val LiveDataSecondaryProgress = MutableLiveData<Int>()
        //设置进度条缓冲条数据源监听
        LiveDataSecondaryProgress.observe(this, { integer: Int -> binding.progressBar.secondaryProgress = integer })

        //创建中介者LiveData,可以监听多个个LiveData的数据变化
        val mediatorLiveData = MediatorLiveData<Int>()
        //添加对LiveDataProgress的监听（这里可以添加多个）
        mediatorLiveData.addSource(LiveDataProgress) { integer: Int -> binding.text1.text = "${integer.toString()}" }
        //同时也可以做为一个liveData，被其他Observer观察。
        mediatorLiveData.observe(this, { integer: Int -> binding.text2.text = "${integer.toString()}" })
        //在子线程中刷新进度条数据
        LifecycleThread(this, Runnable {
            for (i in 0..100) {
                //判断是否标识终止
                if (Thread.currentThread().isInterrupted) //是标识终止,就return结束run()方法
                    return@Runnable
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    //这里的异常多半是使用interrupt()方法引起的,
                    //直接结束run()方法
                    return@Runnable
                }
                Log.i("Thread", i.toString() + "")
                //更新数据(setValue()方法之能在主线程中使用)
                LiveDataProgress.postValue(i)
            }
        }).start()

        //在子线程中刷新进度条缓冲值数据
        //在子线程中刷新中介者的数据
        LifecycleThread(this, Runnable {
            for (i in 0..100) {
                if (Thread.currentThread().isInterrupted) return@Runnable
                try {
                    Thread.sleep(50)
                } catch (e: InterruptedException) {
                    return@Runnable
                }
                LiveDataSecondaryProgress.postValue(i)
                mediatorLiveData.postValue(i)
            }
        }).start()
    }
}