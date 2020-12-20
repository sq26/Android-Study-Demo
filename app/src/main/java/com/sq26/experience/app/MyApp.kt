package com.sq26.experience.app

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.sq26.experience.util.Log

class MyApp : Application() {
    //声明单例模式
    companion object {
        lateinit var app: MyApp
    }

    override fun onCreate() {
        super.onCreate()
        //初始化app
        app = this
        //初始化Fresco图片框架
        Fresco.initialize(this)
        //初始化Logger日志框架
        Log.isEnable = true
        Log.TAG = "APP"

    }
}