package com.sq26.experience.util.permissions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*


/**权限申请
 *
 */
class JPermissions(
    private val context: FragmentActivity
) : LifecycleObserver {
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

    //成功的回调
    private var successCallback: () -> Unit = {}

    //失败的回调
    //successList:成功的列表,failure失败的列表,noPrompt:不能继续申请的权限的列表
    private var failureCallback: (successList: List<String>, failure: List<String>, refuseList: List<String>) -> Unit =
        { _, _, _ -> }

    //设置成功的回调
    fun success(block: () -> Unit): JPermissions {
        this.successCallback = block
        return this
    }

    //设置失败的回调
    fun failure(block: (successList: List<String>, failure: List<String>, refuseList: List<String>) -> Unit): JPermissions {
        this.failureCallback = block
        return this
    }

    //负责请求权限的fragment
    private lateinit var permissionsFragment: PermissionsFragment

    //开始(requestPermissions:要申请的权限)
    fun start(requestPermissions: Array<String>) {
        //可以去申请的权限列表
        val pendingApplicationList = mutableListOf<String>()
        //已被拒绝并且不在提示的权限列表
        val refuseList = mutableListOf<String>()
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
                            context,
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
        //创建申请权限的fragment
        permissionsFragment = PermissionsFragment(
            requestPermissions,{
                successCallback()
                removeFragment()
            },
            {successList, failure, refuse ->
                failureCallback(successList,failure,refuse)
                removeFragment()
            }
        )
        //获取FragmentManager
        val fragmentTransaction =
            context.supportFragmentManager.beginTransaction()
        //将fragment加入到activity中
        fragmentTransaction.add(permissionsFragment, "imageFragment")
        //提交Fragment进行申请
        fragmentTransaction.commit()
    }

    //移除请求权限的fragment
    private fun removeFragment() {
        val fragmentTransaction =
            context.supportFragmentManager.beginTransaction()
        //将fragment从activity中移除
        fragmentTransaction.remove(permissionsFragment)
        //提交
        fragmentTransaction.commit()
    }
}

class PermissionsFragment(
    private val permissionsArray: Array<String>,
    private val success: () -> Unit,
    private val failure: (successList: List<String>, failure: List<String>, refuse: List<String>) -> Unit
) : Fragment() {

    //多权限声明权限的回调
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            //成功的列表
            val successList = mutableListOf<String>()
            //失败的列表
            val failureList = mutableListOf<String>()
            //拒绝的列表
            val refuseList = mutableListOf<String>()
            //遍历权限
            result.forEach {
                //值为true说明已有权限
                if (it.value)
                    successList.add(it.key)
                else {
                    failureList.add(it.key)
                    //shouldShowRequestPermissionRationale判断是否被拒绝弹框,true是会,false不会
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            it.key
                        )
                    ) {
                        refuseList.add(it.key)
                    }
                }
            }
            //成功的数量等于所有的数量表示成功
            if (successList.size == result.size)
                success()
            else
            //失败,需要再次申请或解释
                failure(successList, failureList, refuseList)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //发起权限请求
        requestMultiplePermissions.launch(permissionsArray)
    }
}