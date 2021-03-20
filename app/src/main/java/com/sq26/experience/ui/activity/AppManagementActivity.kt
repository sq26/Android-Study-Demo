package com.sq26.experience.ui.activity

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.sq26.experience.R
import com.sq26.experience.adapter.AppManagementAdapter
import com.sq26.experience.databinding.ActivityAppManagementBinding
import com.sq26.experience.util.Log
import com.sq26.experience.viewmodel.ObservableViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppManagementActivity() : AppCompatActivity() {
    private val appManagementViewModel: AppManagementViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityAppManagementBinding>(
            this,
            R.layout.activity_app_management
        ).apply {
            lifecycleOwner = this@AppManagementActivity
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            toolbar.menu.add("搜索").apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                actionView =
                    SearchView(this@AppManagementActivity).apply {
                        queryHint = "应用名称"
                        isSubmitButtonEnabled = false
                        setIconifiedByDefault(true)
                        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                //点击输入法中的搜索后调用
                                Log.i(query, "query")
                                return true
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                //实时输入回调
                                appManagementViewModel.text = newText.toString()
                                return true
                            }
                        })
//                        val searchPlateId =
//                            this.context.resources.getIdentifier("android:id/search_src_text", null, null)
//                        findViewById<TextView>(searchPlateId).setTextColor(Color.WHITE)
                    }

            }
            toolbar.menu.add("隐藏系统应用").setOnMenuItemClickListener {
                appManagementViewModel.system.apply {
                    it.title = if (this)
                        "显示系统应用"
                    else
                        "隐藏系统应用"
                }
                appManagementViewModel.system = !appManagementViewModel.system
                return@setOnMenuItemClickListener true
            }


            val adapter = AppManagementAdapter()
            recyclerView.adapter = adapter
            appManagementViewModel.appListLiveData.observe(this@AppManagementActivity) {
                adapter.submitList(it)
            }
        }
        appManagementViewModel.initData(this)
    }

}

data class AppInfo(
    var name: String,
    var text: String,
    var icon: Drawable,
    var packageInfo: PackageInfo,
    var system: Boolean
)

@HiltViewModel
class AppManagementViewModel @Inject constructor() : ObservableViewModel() {
    val appListLiveData = MutableLiveData<List<AppInfo>>();
    private val appList = mutableListOf<AppInfo>()

    var text: String = ""
        set(value) {
            field = value
            filter()
        }


    var system: Boolean = true
        set(value) {
            field = value
            filter()
        }

    fun initData(context: Context) {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            list =
//                context.packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
//        } else {
//            list =
//                context.packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)
//        }
        for (item in context.packageManager.getInstalledPackages(0)) {
            val name = item.applicationInfo.loadLabel(context.packageManager).toString()
            appList.add(
                AppInfo(
                    name,
                    "$name\n" +
                            "包名:${item.packageName}\n版本名:${item.versionName}\n" +
                            if ((item.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0)
                                "用户应用"
                            else
                                "系统应用",
                    item.applicationInfo.loadIcon(context.packageManager),
                    item,
                    (item.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                )
            )
        }
        appListLiveData.postValue(appList)
    }

    private fun filter() {
        val list = mutableListOf<AppInfo>()
        for (item in appList) {
            if (text.isEmpty()) {
                if (system) {
                    list.add(item)
                } else {
                    if (!item.system)
                        list.add(item)
                }
            } else {
                if (item.name.contains(text)) {
                    if (system) {
                        list.add(item)
                    } else {
                        if (!item.system)
                            list.add(item)
                    }
                }
            }
        }
        appListLiveData.postValue(list)
    }
}
