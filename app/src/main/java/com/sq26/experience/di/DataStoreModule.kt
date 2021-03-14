package com.sq26.experience.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.createDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.prefs.Preferences

@InstallIn(ActivityRetainedComponent::class)
@Module
class DataStoreModule {

    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        val data:DataStore<Preferences> = context.createDataStore("app",Serializer)
        return data
    }
}