package com.sq26.experience.util.permissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*


/**
 * requestPermissions:要申请的权限
 */
class JPermissions(
    private val context: Context,
    private val requestPermissions: Array<String>
) {
    companion object {
        fun openSettings(context: Context) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri: Uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        }
    }

    //多权限声明权限的回调
    private val requestMultiplePermissions: ActivityResultLauncher<Array<String>> =
        (context as ComponentActivity).registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
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
                            context as Activity,
                            it.key
                        )
                    ) {
                        refuseList.add(it.key)
                    }
                }
            }
            //成功的数量等于所有的数量表示成功
            if (successList.size == result.size)
                successCallback.invoke()
            else
            //失败,需要再次申请或解释
                failureCallback.invoke(successList, failureList, refuseList)
        }

    //成功的回调
    private lateinit var successCallback: () -> Unit

    //失败的回调
    //successList:成功的列表,failure失败的列表,noPrompt:不能继续申请的权限的列表
    private lateinit var failureCallback: (successList: List<String>, failure: List<String>, noPrompt: List<String>) -> Unit

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
            successCallback.invoke()
            return
        }
        //没有需要申请的权限,有无法申请的权限
        if (pendingApplicationList.size == 0) {
            failureCallback.invoke(listOf(), refuseList, refuseList)
            return
        }
        //申请权限
        requestMultiplePermissions.launch(pendingApplicationList.toTypedArray())
    }
}