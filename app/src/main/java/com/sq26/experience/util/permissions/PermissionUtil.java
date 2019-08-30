package com.sq26.experience.util.permissions;

import android.Manifest;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class PermissionUtil {
    //允许程序读取用户的日程信息
    public static final String READ_CALENDAR = Manifest.permission.READ_CALENDAR;
    //允许程序写入日程，但不可读取
    public static final String WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR;
    //允许程序访问摄像头进行拍照
    public static final String CAMERA = Manifest.permission.CAMERA;
    //允许程序访问联系人通讯录信息
    public static final String READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    //写入联系人,但不可读取
    public static final String WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS;
    //允许程序访问账户Gmail列表
    public static final String GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    //允许程序通过GPS芯片接收卫星的定位信息
    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    //允许程序通过WiFi或移动基站的方式获取用户错略的经纬度信息
    public static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    //允许程序录制声音通过手机或耳机的麦克
    public static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    //允许程序访问电话状态
    public static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    //允许程序从非系统拨号器里拨打电话
    public static final String CALL_PHONE = Manifest.permission.CALL_PHONE;
    //读取通话记录
    public static final String READ_CALL_LOG = Manifest.permission.READ_CALL_LOG;
    //允许程序写入（但是不能读）用户的联系人数据
    public static final String WRITE_CALL_LOG = Manifest.permission.WRITE_CALL_LOG;
    //允许一个应用程序添加语音邮件系统
    public static final String ADD_VOICEMAIL = Manifest.permission.ADD_VOICEMAIL;
    //允许程序使用SIP视频服务
    public static final String USE_SIP = Manifest.permission.USE_SIP;
    //允许程序监视，修改或放弃播出电话
    public static final String PROCESS_OUTGOING_CALLS = Manifest.permission.PROCESS_OUTGOING_CALLS;
    //允许该应用存取监测您身体状况的传感器所收集的数据，例如您的心率。
    public static final String BODY_SENSORS = Manifest.permission.BODY_SENSORS;
    //允许程序发送短信
    public static final String SEND_SMS = Manifest.permission.SEND_SMS;
    //允许程序接收短信
    public static final String RECEIVE_SMS = Manifest.permission.RECEIVE_SMS;
    //允许程序读取短信内容
    public static final String READ_SMS = Manifest.permission.READ_SMS;
    //允许程序接收WAP PUSH信息
    public static final String RECEIVE_WAP_PUSH = Manifest.permission.RECEIVE_WAP_PUSH;
    //允许程序接收彩信
    public static final String RECEIVE_MMS = Manifest.permission.RECEIVE_MMS;
    //程序可以读取设备外部存储空间
    public static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    //允许程序写入外部存储
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    //允许您的应用读取设备中存储的电话号码。
    public static final String READ_PHONE_NUMBERS = Manifest.permission.READ_PHONE_NUMBERS;
    //允许您的应用通过编程方式接听呼入电话。要在您的应用中处理呼入电话，您可以使用 acceptRingingCall() 函数。
    public static final String ANSWER_PHONE_CALLS = Manifest.permission.ANSWER_PHONE_CALLS;

    public static final class Group {
        //日历数据
        //android.permission.READ_CALENDAR
        //android.permission.WRITE_CALENDAR
        public static final String CALENDAR = Manifest.permission_group.CALENDAR;
        //相机
        //android.permission.CAMERA
        public static final String CAMERA = Manifest.permission_group.CAMERA;
        //联系人
        //android.permission.READ_CONTACTS
        //android.permission.WRITE_CONTACTS
        //android.permission.GET_ACCOUNTS
        public static final String CONTACTS = Manifest.permission_group.CONTACTS;
        //位置
        //android.permission.ACCESS_FINE_LOCATION
        //android.permission.ACCESS_COARSE_LOCATION
        public static final String LOCATION = Manifest.permission_group.LOCATION;
        //麦克风
        //android.permission.RECORD_AUDIO
        public static final String MICROPHONE = Manifest.permission_group.MICROPHONE;
        //电话
        //android.permission.READ_PHONE_STATE
        //android.permission.CALL_PHONE
        //android.permission.READ_CALL_LOG
        //android.permission.WRITE_CALL_LOG
        //com.android.voicemail.permission.ADD_VOICEMAIL
        //android.permission.USE_SIP
        //android.permission.PROCESS_OUTGOING_CALLS
        public static final String PHONE = Manifest.permission_group.PHONE;
        //传感器
        //android.permission.BODY_SENSORS
        public static final String SENSORS = Manifest.permission_group.SENSORS;
        //短信
        //android.permission.SEND_SMS
        //android.permission.RECEIVE_SMS
        //android.permission.READ_SMS
        //android.permission.RECEIVE_WAP_PUSH
        //android.permission.RECEIVE_MMS
        //android.permission.READ_CELL_BROADCASTS
        public static final String SMS = Manifest.permission_group.SMS;
        //存储
        //android.permission.READ_EXTERNAL_STORAGE
        //android.permission.WRITE_EXTERNAL_STORAGE
        public static final String STORAGE = Manifest.permission_group.STORAGE;
    }

    //打开到对应的设置界面



}
