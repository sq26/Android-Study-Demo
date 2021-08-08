package com.sq26.experience.di

import android.content.Context
import com.sq26.experience.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//Module注解,声明Hilt 模块,它会告知 Hilt 如何提供某些类型的实例。
//InstallIn注解,以告知 Hilt 每个(这个)模块将用在或安装在哪个 Android 类中。
//在 Hilt 模块中提供的依赖项可以在生成的所有与 Hilt 模块安装到的 Android 类关联的组件中使用。
@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    //Singleton注解,表示此方法是挂在application生命周期下的方法,在整个app中只会实例化一次,表示所有Android组件使用同一个实例
    //Provides注解,告知 Hilt 如何提供此类型的实例,方法名无所谓,重点是返回类型表示返回哪个类的实例,参数是创建实例需要的依赖项
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        //在这里写提供实例的代码
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideDownloadDao(appDatabase: AppDatabase): DownloadDao {
        return appDatabase.downloadDao()
    }
}