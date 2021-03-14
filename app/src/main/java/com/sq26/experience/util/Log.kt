package com.sq26.experience.util

import android.util.Log
import com.sq26.experience.util.Log.Companion.i
import java.util.*

class Log {
    companion object {
        //默认tag,可以在app中初始化
        var TAG = "app"

        //是否启用打印
        var isEnable = true

        //详情(黑色)
        fun i(msg: Any?="null", tag: String = TAG, isThread: Boolean = false) {
            print(Log.INFO, tag, msg.toString(), isThread)
        }

        //调试(黑色)
        fun d(msg: String, tag: String = TAG, isThread: Boolean = false) {
            print(Log.DEBUG, tag, msg, isThread)
        }

        //错误(红色)
        fun e(msg: String, tag: String = TAG, isThread: Boolean = false) {
            print(Log.ERROR, tag, msg, isThread)
        }

        //警告(蓝色)
        fun w(msg: String, tag: String = TAG, isThread: Boolean = false) {
            print(Log.WARN, tag, msg, isThread)
        }

        //冗长的(黑色)
        fun v(msg: String, tag: String = TAG, isThread: Boolean = false) {
            print(Log.VERBOSE, tag, msg, isThread)
        }

        //最大大小
        private const val size = 4000

        //打印
        private fun print(priority: Int, tag: String, msg: String, isThread: Boolean) {
            if (!isEnable)
                return
            //起始下标
            var index = 0
            //是否打印线程信息
            if (isThread)
                Log.println(priority, tag, "ThreadName:${Thread.currentThread().name}")
            //遍历(下标小于msg长度)
            while (index < msg.length) {
                //打印日志
                if (msg.length - index > size)
                    Log.println(priority, tag, msg.substring(index, index + size))
                else
                    Log.println(priority, tag, msg.substring(index))
                index += size
            }
        }
    }
}

//给string类添加简单打印
fun String.log() {
    i(this)
}