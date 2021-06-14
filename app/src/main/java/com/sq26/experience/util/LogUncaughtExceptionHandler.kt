package com.sq26.experience.util

import android.content.Context
import okio.internal.commonAsUtf8ToByteArray

class LogUncaughtExceptionHandler(private val handler: () -> Unit) :
    Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        "有致命异常".i()
//        e.printStackTrace()
        handler()
    }
}