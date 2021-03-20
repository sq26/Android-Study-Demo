package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import com.sq26.experience.aidl.IAidlInterface
import android.os.Bundle
import android.content.Intent
import android.content.ComponentName
import com.sq26.experience.aidl.ICallbackAidlInterface
import kotlin.Throws
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import com.sq26.experience.BuildConfig
import com.sq26.experience.databinding.ActivityAidlBinding
import java.lang.StringBuilder

class AIDLActivity : AppCompatActivity() {

    //声明aidi服务方法
    private var iAidlInterface: IAidlInterface? = null

    //用于储存消息记录
    private val stringBuilder = StringBuilder()

    //定义注册名称
    private val name = System.currentTimeMillis().toString() + ""
    //视图绑定
    private lateinit var binding: ActivityAidlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAidlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //设置buttom的点击事件
        binding.button.setOnClickListener {
            if (binding.editText.text.isNotEmpty()) {
                try {
                    //发送信息
                    iAidlInterface?.pushText(name + ":" + binding.editText.text.toString())
                    //清空输入框
                    binding.editText.setText("")
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }
        //初始化服务连接
        init()
    }

    private fun init() {
        val intent = Intent()
        val componentName =
            ComponentName(packageName, "com.sq26.experience.service.AIDLService")
        intent.component = componentName
        //创建aidi回调实现
        val iCallbackAidlInterface: ICallbackAidlInterface.Stub =
            object : ICallbackAidlInterface.Stub() {
                @Throws(RemoteException::class)
                override fun pull(text: String) {
                    //接受服务端发送过来的新信息
                    stringBuilder.append(text)
                    //换行
                    stringBuilder.append("\n")
                    //显示文本
                    binding.textView.text = stringBuilder.toString()
                }
            }
        //创建服务连接
        val serviceConnection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
                //服务连接的回调
                //从服务连接获取服务的aidi实现
                iAidlInterface = IAidlInterface.Stub.asInterface(iBinder)
                try {
                    //注册回调
                    iAidlInterface?.registerCallback(
                        name,
                        iCallbackAidlInterface
                    )
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                //服务断开的回调
            }
        }

        //绑定服务
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            //注销回调
            iAidlInterface?.unregisterCallback(name)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
}