package com.sq26.experience.ui.navigation

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sq26.experience.R
import com.sq26.experience.app.AppDataStoreKey
import com.sq26.experience.app.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { context ->
            //返回对此资源对象有效的当前配置。
            val config = context.resources.configuration
            //获取系统默认语言
            val sysLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.locales.get(0)
            } else {
                config.locale
            }
            //获取用户设置语言
            val language =
                runBlocking { context.dataStore.data.first()[AppDataStoreKey.language].orEmpty() }
            //用户设置过语言并且用户设置的语言与系统语言不同就修改配置语言
            if (language.isNotEmpty() && language != sysLocale.language) {
                //创建用户选择语言的Locale对象
                val locale = Locale(language)
                //设置语言
                config.setLocale(locale)
                //为当前上下文返回一个新的上下文对象，但其资源已调整以匹配给定的配置。
                // 对该方法的每次调用都返回一个上下文对象的新实例；
                context.createConfigurationContext(config)
            }else{
                context
            }
        })
    }
}