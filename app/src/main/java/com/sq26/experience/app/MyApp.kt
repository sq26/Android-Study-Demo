package com.sq26.experience.app

import android.app.Application
import android.os.Environment
import com.facebook.drawee.backends.pipeline.Fresco
import com.sq26.experience.util.Log
import com.sq26.experience.util.LogUncaughtExceptionHandler
import com.sq26.experience.util.i
import com.sq26.experience.util.kotlin.toast
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import kotlin.concurrent.thread
import kotlin.system.exitProcess

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

        val logFile = File(getExternalFilesDir("log"), "${System.currentTimeMillis()}.txt")
        logFile.absolutePath.i()
        //设置默认异常处理接口
        Thread.setDefaultUncaughtExceptionHandler(LogUncaughtExceptionHandler{
            toast("出现意外,5秒钟后退出!")
            thread {
                Thread.sleep(5000)
                android.os.Process.killProcess(android.os.Process.myPid())
            }
        })
//        Log.readLog(logFile)
    }
}