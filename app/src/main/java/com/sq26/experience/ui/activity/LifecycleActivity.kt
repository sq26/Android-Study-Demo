package com.sq26.experience.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.sq26.experience.R

class LifecycleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle)
        //监听生命周期
        //方式1(注解方式实现)(不被官方推荐)
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {
                Log.i("1", "onCreate")
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                Log.i("1", "onStart")
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                Log.i("1", "onResume")
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                Log.i("1", "onPause")
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
                Log.i("1", "onStop")
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                Log.i("1", "onDestroy")
            }
        })
        //方式二,继承DefaultLifecycleObserver实现(官方推荐)
        lifecycle.addObserver(MainPresenter())
    }

    internal class MainPresenter : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            Log.i("2", "onCreate")
        }

        override fun onStart(owner: LifecycleOwner) {
            Log.i("2", "onStart")
        }

        override fun onResume(owner: LifecycleOwner) {
            Log.i("2", "onResume")
        }

        override fun onPause(owner: LifecycleOwner) {
            Log.i("2", "onPause")
        }

        override fun onStop(owner: LifecycleOwner) {
            Log.i("2", "onStop")
        }

        override fun onDestroy(owner: LifecycleOwner) {
            Log.i("2", "onDestroy")
        }
    }
}