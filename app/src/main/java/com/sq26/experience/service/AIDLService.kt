package com.sq26.experience.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import com.sq26.experience.aidl.IAidlInterface
import com.sq26.experience.aidl.ICallbackAidlInterface
import java.util.concurrent.ConcurrentHashMap

class AIDLService : Service() {
    //定义一个线程安全的map用于保存通讯回调
    private val map: MutableMap<String, ICallbackAidlInterface> = ConcurrentHashMap()

    //实现aidi接口
    private val iAidlInterface: IAidlInterface.Stub = object : IAidlInterface.Stub() {
        @Throws(RemoteException::class)
        override fun pushText(text: String) {
            //发送信息
            for (key in map.keys) {
                map[key]?.pull(text)
            }
        }

        @Throws(RemoteException::class)
        override fun registerCallback(name: String, callback: ICallbackAidlInterface) {
            //注册信息接收回调
            map[name] = callback
        }

        @Throws(RemoteException::class)
        override fun unregisterCallback(name: String) {
            //注销信息接收回调
            map.remove(name)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        //返回服务接口实现
        return iAidlInterface
    }
}