package com.sq26.experience.viewmodel

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sq26.experience.data.HomeMenu
import com.sq26.experience.data.HomeMenuDao
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

class HomeViewModel @ViewModelInject internal constructor(
    private val homeRepository: HomeRepository,
    @ActivityContext private val context: Context
) : ViewModel() {
    val content = "主页"
    val title = "首页"

    val homeMenuList = MutableLiveData<List<HomeMenu>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            homeMenuList.postValue(homeRepository.getHomeMenuList("0"))
        }
    }

    fun refreshHomeMenuList(typeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            homeMenuList.postValue(homeRepository.getHomeMenuList(typeId))
        }
    }


    val homeMenuTypeList: LiveData<List<HomeMenu>> =
        homeRepository.getHomeMenuTypeList().asLiveData()
//        liveData {
//        emit(homeRepository.getHomeMenuList())

//        val list = listOf(HomeMenu("kotlin学习", "用来做kotlin语言学习"))
//        emit(list)
//    }
}

//Singleton注解会将HomeRepository的生命周期绑定到application,也就是回和应用程序一起创建和销毁,是一种更加方便地单例模式,所有依赖HomeRepository的类获取到的都是同一实例
//Inject注解会使用设置在DatabaseModule中的HomeMenuDao的获取方法
@Singleton
class HomeRepository @Inject constructor(
    private val homeMenuDao: HomeMenuDao
) {

    fun getHomeMenuList(typeId: String) = homeMenuDao.getHomeMenuList(typeId)

    fun getHomeMenuTypeList() = homeMenuDao.getHomeMenuTypeList()
}