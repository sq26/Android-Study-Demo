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
            val config = context.resources.configuration
            val sysLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.locales.get(0)
            } else {
                config.locale
            }
            val language =
                runBlocking { context.dataStore.data.first()[AppDataStoreKey.language].orEmpty() }
            if (language.isNotEmpty() && language != sysLocale.language) {
                val locale = Locale(language)
//                Locale.setDefault(locale)
                config.setLocale(locale)
                context.createConfigurationContext(config)
            }else{
                context
            }
        })
    }
}