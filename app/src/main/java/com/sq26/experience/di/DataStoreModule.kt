package com.sq26.experience.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@InstallIn(ActivityRetainedComponent::class)
@Module
class DataStoreModule {

//    @Provides
//    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
//        val data:DataStore<Preferences> = context.createDataStore("app",Serializer)
//        return data
//    }
}