package com.sq26.experience.util.permissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.sq26.experience.util.Log
import java.util.*


/**
 * requestPermissions:要申请的权限
 */
class JPermissions(
    private val context: Context,
    requestPermissions: Array<String> = arrayOf()
) :LifecycleObserver {

    //获取权限请求的viewModel
    private val viewModel: PermissionsViewModel by (context as ComponentActivity).viewModels()

    //要请求的权限列表
    private var requestPermissions: Array<String> = arrayOf()

    //请求完毕后的回调
    private val observable = Observer<Boolean> {
        //true是成功
        if (it)
            successCallback()
        else
            failureCallback(viewModel.successList, viewModel.failureList, viewModel.refuseList)
        removeFragment()
    }

    init {
        //初始化权限列表
        this.requestPermissions = requestPermissions
        //设置请求完毕后的回调
        viewModel.result.observeForever(observable)

        (context as ComponentActivity).lifecycle.addObserver(this)
    }

    //在宿主结束前移除监听
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){
        if (viewModel.result.hasObservers())
            viewModel.result.removeObserver(observable)
    }

    companion object {
        //打开应用的系统设置
        fun openSettings(context: Context) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri: Uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        }
    }

    //设置要请求的权限列表
    fun setRequestPermissions(requestPermissions: Array<String>): JPermissions {
        this.requestPermissions = requestPermissions
        return this
    }

    //成功的回调
    private var successCallback: () -> Unit = {}

    //失败的回调
    //successList:成功的列表,failure失败的列表,noPrompt:不能继续申请的权限的列表
    private var failureCallback: (successList: List<String>, failure: List<String>, noPrompt: List<String>) -> Unit =
        { _, _, _ -> }

    //设置成功的回调
    fun success(block: () -> Unit): JPermissions {
        this.successCallback = block
        return this
    }

    //设置失败的回调
    fun failure(block: (successList: List<String>, failure: List<String>, noPrompt: List<String>) -> Unit): JPermissions {
        this.failureCallback = block
        return this
    }

    //负责请求权限的fragment
    private val permissionsFragment = PermissionsFragment()

    //开始
    fun start() {
        //可以去申请的权限列表
        val pendingApplicationList: MutableList<String> = ArrayList()
        //已被拒绝并且不在提示的权限列表
        val refuseList: MutableList<String> = ArrayList()
        //checkSelfPermission检擦是否有权限
        for (s in requestPermissions)
        //PackageManager.PERMISSION_GRANTED有权限
        //PackageManager.PERMISSION_DENIED 无权限
            if (ContextCompat.checkSelfPermission(
                    context,
                    s
                ) == PackageManager.PERMISSION_DENIED
            ) {
                //未被授权
                //适配Android 23 6.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //shouldShowRequestPermissionRationale判断是否被拒绝弹框,true是会,false不会
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            context as Activity,
                            s
                        )
                    ) {
                        //不会弹框,需要解释
                        refuseList.add(s)
                    } else {
                        //会弹框
                        pendingApplicationList.add(s)
                    }
                } else {
                    //不会弹框,需要解释
                    refuseList.add(s)
                }
            }

        //判断是否所有要申请的权限都需要申请
        if (pendingApplicationList.size == 0 && refuseList.size == 0) {
            //要申请的权限都已拥有权限,只接调用成功回调
            successCallback()
            return
        }
        //没有需要申请的权限,有无法申请的权限
        if (pendingApplicationList.size == 0) {
            failureCallback(listOf(), refuseList, refuseList)
            return
        }
        //申请权限
        //获取FragmentManager
        val fragmentTransaction =
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        //将fragment加入到activity中
        fragmentTransaction.add(permissionsFragment, "imageFragment")
        //提交
        fragmentTransaction.commit()
        //提交到Fragment进行正式请求

        Log.i("2")
        viewModel.permissionsArray.postValue(pendingApplicationList.toTypedArray())
    }

    //移除请求权限的fragment
    private fun removeFragment() {
        val fragmentTransaction =
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        //将fragment从activity中移除
        fragmentTransaction.remove(permissionsFragment)
        //提交
        fragmentTransaction.commit()
        //移除监听
        viewModel.result.removeObserver(observable)
    }
}

//权限请求的viewmo
class PermissionsViewModel : ViewModel() {
    //权限列表LiveData,负责发送要申请的权限
    val permissionsArray = MutableLiveData<Array<String>>()

    //返回结果的LiveData
    val result = MutableLiveData<Boolean>()

    //成功的列表
    var successList = mutableListOf<String>()

    //失败的列表
    var failureList = mutableListOf<String>()

    //拒绝的列表
    var refuseList = mutableListOf<String>()

    override fun onCleared() {

        super.onCleared()
    }
}

class PermissionsFragment : Fragment() {
    val viewModel: PermissionsViewModel by activityViewModels()
    //多权限声明权限的回调
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            //清空缓存的列表数据
            viewModel.successList.clear()
            viewModel.failureList.clear()
            viewModel.refuseList.clear()
            //遍历权限
            result.forEach {
                //值为true说明已有权限
                if (it.value)
                    viewModel.successList.add(it.key)
                else {
                    viewModel.failureList.add(it.key)
                    //shouldShowRequestPermissionRationale判断是否被拒绝弹框,true是会,false不会
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            context as Activity,
                            it.key
                        )
                    ) {
                        viewModel.refuseList.add(it.key)
                    }
                }
            }
            //成功的数量等于所有的数量表示成功
            if (viewModel.successList.size == result.size)
                viewModel.result.postValue(true)
            else
            //失败,需要再次申请或解释
                viewModel.result.postValue(false)
        }

    private val observable = Observer<Array<String>> {
        //发起权限请求
        requestMultiplePermissions.launch(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //注册权限请求监听
        viewModel.permissionsArray.observeForever(observable)
    }
    //Fragment结束移除监听
    override fun onDestroy() {
        viewModel.permissionsArray.removeObserver(observable)
        super.onDestroy()
    }
}