package com.sq26.experience.app

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.sq26.experience.util.Log
import dagger.hilt.android.HiltAndroidApp
//@HiltAndroidApp 会触发 Hilt 的代码生成操作，生成的代码包括应用的一个基类，该基类充当应用级依赖项容器。
@HiltAndroidApp
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
    }
}