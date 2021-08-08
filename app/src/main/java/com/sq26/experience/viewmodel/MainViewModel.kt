package com.sq26.experience.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import com.sq26.experience.R
import com.sq26.experience.entity.HomeMenu
import com.sq26.experience.ui.activity.*
import com.sq26.experience.ui.activity.file.FileHomeActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    val title = "首页"

    private val menuAll = listOf(
        HomeMenu("0", "技术功能", 0),
        HomeMenu(
            "encryption",
            context.getString(R.string.Symmetric_and_asymmetric_encryption),
            1,
            "0"
        ),
        HomeMenu("aidl", "AIDL进程间通讯", 1, "0"),
        HomeMenu("javaTest", "java测试", 1, "0"),
        HomeMenu("kotlin", "kotlin语言学习", 1, "0"),
        HomeMenu("1", "框架功能", 0),
        HomeMenu("Navigation", "Navigation导航框架", 1, "1"),
        HomeMenu("Paging", "Paging框架(RecyclerView分页库)", 1, "1"),
        HomeMenu("WorkManger", "WorkManger框架(后台任务库)", 1, "1"),
        HomeMenu("DataBinding", "Data Binding框架(数据绑定)", 1, "1"),
        HomeMenu("DataStore", "DataStore框架(数据储存)", 1, "1"),
        HomeMenu("2", "android功能", 0),
        HomeMenu("camera", context.getString(R.string.camera), 1, "2"),
        HomeMenu("statusBar", "侵入式体验(通知栏和导航栏控制)", 1, "2"),
        HomeMenu("authorizedOperation", "授权操作", 1, "2"),
        HomeMenu("fileManagement", "文件管理", 1, "2"),
        HomeMenu("downloadManagement", "下载管理", 1, "2"),
        HomeMenu("network", "网络", 1, "2"),
        HomeMenu("WiFiDirect", "WIFI直连", 1, "2"),
        HomeMenu("AppManagement", "app管理", 1, "2"),
        HomeMenu("MediaOperating", "媒体操作", 1, "2"),
        HomeMenu("notification", "通知", 1, "2"),
        HomeMenu("WebView", "WebView交互", 1, "2"),
        HomeMenu("3", "视图", 0),
        HomeMenu("pullToRefresh", "下拉刷新,上拉加载更多", 1, "3"),
        HomeMenu("RecyclerView", "RecyclerView的使用", 1, "3"),
        HomeMenu("MotionLayout", "MotionLayout的使用", 1, "3")
    )

    //获取菜单类型数据
    val homeMenuTypeList = MutableLiveData<List<HomeMenu>>()

    //获取菜单数据对应菜单数据
    val homeMenuList = MutableLiveData<List<HomeMenu>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            homeMenuTypeList.postValue(menuAll.filter { it.type == 0 })
            refreshHomeMenuList("0")
        }
    }

    //刷新数据
    fun refreshHomeMenuList(typeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            homeMenuList.postValue(menuAll.filter { it.typeId == typeId })
        }
    }


    fun startTo(context: Context, id: String) {
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
            "downloadManagement" ->
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
            "MotionLayout" ->
                intent.setClass(context, MotionLayoutActivity::class.java)
            "MediaOperating" ->
                intent.setClass(context, MediaOperatingActivity::class.java)
            "notification" ->
                intent.setClass(context, NotificationActivity::class.java)
        }
        context.startActivity(intent)
    }
}