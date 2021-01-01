package com.sq26.experience.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.sq26.experience.data.HomeMenu
import com.sq26.experience.data.HomeMenuDao
import javax.inject.Inject
import javax.inject.Singleton

class HomeViewModel @ViewModelInject internal constructor(
    homeRepository: HomeRepository
) : ViewModel() {
    val content = "主页"
    val left = "左边栏"
    val title = "首页"

    val homeMenuList: LiveData<List<HomeMenu>> = homeRepository.getHomeMenuList().asLiveData()
//        liveData {
//        emit(homeRepository.getHomeMenuList())

//        val list = listOf(HomeMenu("kotlin学习", "用来做kotlin语言学习"))
//        emit(list)
//    }
}

//Singleton注解会将HomeRepository的生命周期绑定到application,也就是回和应用程序一起创建和销毁,是一种更加方便地单例模式,所有依赖HomeRepository的类获取到的都是同一实例
@Singleton
class HomeRepository @Inject constructor(
    private val homeMenuDao: HomeMenuDao
) {
    fun getHomeMenuList() = homeMenuDao.getHomeMenuList()
}