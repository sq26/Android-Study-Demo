package com.sq26.experience.app

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
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
//        Thread.setDefaultUncaughtExceptionHandler(LogUncaughtExceptionHandler{
//            toast("出现意外,5秒钟后退出!")
//            thread {
//                Thread.sleep(5000)
//                android.os.Process.killProcess(android.os.Process.myPid())
//            }
//        })
//        Log.readLog(logFile)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "download"
            val channelName = "下载通知"

            /**
             * 通知权限, 数值越高，提示权限就越高
             * IMPORTANCE_DEFAULT= 3;
             * IMPORTANCE_HIGH = 4;
             * IMPORTANCE_LOW = 2;
             * IMPORTANCE_MAX = 5;
             * IMPORTANCE_MIN = 1;
             * IMPORTANCE_NONE = 0;
             * IMPORTANCE_UNSPECIFIED = -1000;
             */
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "下载时显示进度以及操作"
                //是否开启指示灯（是否在桌面icon右上角展示小红点）
                enableLights(false)
                //是否开启震动
                enableVibration(false)
                //设置绕过免打扰模式
                setBypassDnd(true)
                //设置是否应在锁定屏幕上显示此频道的通知,显示
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                //设置是否显示角标
                setShowBadge(true)
            }

            //获取通知服务
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //创建通知渠道
            notificationManager.createNotificationChannel(notificationChannel)

        }

    }
}