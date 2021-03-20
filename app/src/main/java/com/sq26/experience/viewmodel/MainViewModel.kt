package com.sq26.experience.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import com.sq26.experience.data.HomeMenu
import com.sq26.experience.data.HomeMenuDao
import com.sq26.experience.ui.activity.*
import com.sq26.experience.ui.activity.file.FileHomeActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class MainViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {
    val title = "首页"

    //获取数据
    val homeMenuList = MutableLiveData<List<HomeMenu>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            homeMenuList.postValue(homeRepository.getHomeMenuList("0"))
        }
    }

    //刷新数据
    fun refreshHomeMenuList(typeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            homeMenuList.postValue(homeRepository.getHomeMenuList(typeId))
        }
    }

    //获取菜单类型数据
    val homeMenuTypeList: LiveData<List<HomeMenu>> =
        homeRepository.getHomeMenuTypeList().asLiveData()

    fun startTo(context: Context,id: String) {
        val intent = Intent(context, EncryptionActivity::class.java)
        //在新的窗口打开
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        when (id) {
            "encryption" ->
                intent.setClass(context, EncryptionActivity::class.java)
            "aidl" ->
                intent.setClass(context, AIDLActivity::class.java)
            "javaTest" ->
                intent.setClass(context, TestActivity::class.java)
            "kotlin" ->
                intent.setClass(context, KotlinActivity::class.java)
            "Navigation" ->
                intent.setClass(context, NavigationActivity::class.java)
            "Paging" ->
                intent.setClass(context, PagingActivity::class.java)
            "DataBinding" ->
                intent.setClass(context, DataBindingActivity::class.java)
            "camera" ->
                intent.setClass(context, CameraActivity::class.java)
            "statusBar" ->
                intent.setClass(context, StatusBarActivity::class.java)
            "authorizedOperation" ->
                intent.setClass(context, AuthorizedOperationActivity::class.java)
            "fileManagement" ->
                intent.setClass(context, FileHomeActivity::class.java)
            "downloadManagement"
            ->
                intent.setClass(context, DownloadManagementActivity::class.java)
            "network" ->
                intent.setClass(context, NetworkActivity::class.java)
            "WiFiDirect" ->
                intent.setClass(context, WiFiDirectActivity::class.java)
            "AppManagement" ->
                intent.setClass(context, AppManagementActivity::class.java)
            "pullToRefresh" ->
                intent.setClass(context, PullToRefreshActivity::class.java)
            "RecyclerView" ->
                intent.setClass(context, RecyclerViewActivity::class.java)
            "WorkManger" ->
                intent.setClass(context, WorkManagerActivity::class.java)
            "DataStore" ->
                intent.setClass(context, DataStoreActivity::class.java)
        }
        context.startActivity(intent)
    }
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