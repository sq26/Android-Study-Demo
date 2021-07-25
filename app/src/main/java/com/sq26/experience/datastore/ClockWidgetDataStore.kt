package com.sq26.experience.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

//创建时钟组件的持久化储存
val Context.clockWidgetDataStore: DataStore<Preferences> by preferencesDataStore(name = "clockWidget")

object ClockWidgetDataStoreKey {
    val appWidgetIds = stringSetPreferencesKey("appWidgetIds")
    val wallpaperMap = stringPreferencesKey("textSizeMap")
}