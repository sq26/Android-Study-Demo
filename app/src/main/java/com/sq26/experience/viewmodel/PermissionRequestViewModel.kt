package com.sq26.experience.viewmodel

import android.Manifest
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.sq26.experience.util.permissions.JPermissions


class PermissionRequestViewModel : ViewModel() {
    //日历数据
    var calendarData = false

    //相机
    var camera = false

    //联系人
    var contactPerson = false

    //位置
    var position = false

    //麦克风
    var microphone = false

    //电话
    var phone = false

    //传感器
    var sensor = false

    //短信
    var SMS = false

    //存储
    var storage = false

    //申请权限
    fun startPermissions(context: FragmentActivity) {
        val requestPermissions = mutableListOf<String>()
        if (calendarData) {
            //允许程序读取用户的日程信息
            requestPermissions.add(Manifest.permission.READ_CALENDAR)
            //允许程序写入日程，但不可读取
            requestPermissions.add(Manifest.permission.WRITE_CALENDAR)
        }
        if (camera)
        //允许程序访问摄像头
            requestPermissions.add(Manifest.permission.CAMERA)
        if (contactPerson) {
            //允许程序访问联系人通讯录信息
            requestPermissions.add(Manifest.permission.READ_CONTACTS)
            //写入联系人,但不可读取
            requestPermissions.add(Manifest.permission.WRITE_CONTACTS)
            //允许程序访问账户Gmail列表
            requestPermissions.add(Manifest.permission.GET_ACCOUNTS)
        }
        if (position) {
            //允许程序通过GPS芯片接收卫星的定位信息
            requestPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            //允许程序通过WiFi或移动基站的方式获取用户错略的经纬度信息
//            requestPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (microphone) {
            //允许程序录制声音通过手机或耳机的麦克
            requestPermissions.add(Manifest.permission.RECORD_AUDIO)
        }
        if (phone) {
            //允许程序访问电话状态
            requestPermissions.add(Manifest.permission.READ_PHONE_STATE)
            //允许程序从非系统拨号器里拨打电话
            requestPermissions.add(Manifest.permission.CALL_PHONE)
            //读取通话记录
            requestPermissions.add(Manifest.permission.READ_CALL_LOG)
            //允许程序写入（但是不能读）用户的通话记录
            requestPermissions.add(Manifest.permission.WRITE_CALL_LOG)
            //允许一个应用程序添加语音邮件系统
            requestPermissions.add(Manifest.permission.ADD_VOICEMAIL)
            //允许程序使用SIP视频服务
            requestPermissions.add(Manifest.permission.USE_SIP)
            //android 8.0以上功能
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                //允许您的应用读取设备中存储的电话号码。
                requestPermissions.add(Manifest.permission.READ_PHONE_NUMBERS)
                //允许您的应用通过编程方式接听呼入电话。要在您的应用中处理呼入电话，您可以使用 acceptRingingCall() 函数。
                requestPermissions.add(Manifest.permission.ANSWER_PHONE_CALLS)
            }
        }
        if (sensor)
        //允许该应用存取监测您身体状况的传感器所收集的数据，例如您的心率。
            requestPermissions.add(Manifest.permission.BODY_SENSORS)
        if (SMS) {
            //允许程序发送短信
            requestPermissions.add(Manifest.permission.SEND_SMS)
            //允许程序接收短信
            requestPermissions.add(Manifest.permission.RECEIVE_SMS)
            //允许程序读取短信内容
            requestPermissions.add(Manifest.permission.READ_SMS)
            //允许程序接收WAP PUSH信息
            requestPermissions.add(Manifest.permission.RECEIVE_WAP_PUSH)
            //允许程序接收彩信
            requestPermissions.add(Manifest.permission.RECEIVE_MMS)
        }
        if (storage) {
            //程序可以读取设备外部存储空间
            requestPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            //允许程序写入外部存储
            requestPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        JPermissions(context)
            .success {
                AlertDialog.Builder(context).setMessage("申请成功").setPositiveButton("确定", null).show()
            }
            .failure { successList, failure, noPrompt ->
                AlertDialog.Builder(context).setTitle("申请失败")
                    .setMessage("成功的权限:$successList\n失败的权限:$failure\n被永久拒绝的权限:$noPrompt")
                    .setPositiveButton("确定", null)
                    .setNegativeButton("打开权限设置页面") { _, _ ->
                        JPermissions.openSettings(context)
                    }
                    .show()
            }
            .start(requestPermissions.toTypedArray())
    }
}