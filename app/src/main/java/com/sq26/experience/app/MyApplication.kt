package com.sq26.experience.app

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //初始化Fresco图片框架
        Fresco.initialize(this)
        //初始化Logger日志框架
        Logger.addLogAdapter(object :AndroidLogAdapter(PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)//（可选）是否显示线程信息。 默认值为true
                .methodCount(0)//（可选）要显示的方法行数。 默认2
                .methodOffset(5)        // （可选）隐藏内部方法调用到偏移量。 默认5
                .tag("L")//（可选）每个日志的全局标记。 默认PRETTY_LOGGER
                .build()){
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                //在这里开启或关闭日志系统
                return true
            }
        })
//        Logger.i("information")//信息
//        Logger.d("debug")//调试
//        Logger.w("warning")//警告
//        Logger.e("error")//错误
//        Logger.v("verbose")//冗长的
//        Logger.wtf("What a Terrible Failure")//多么可怕的失败
//        Logger.t("tag").d("ddd")//t是额外tag标记

    }
}