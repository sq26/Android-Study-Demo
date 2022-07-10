package com.sq26.experience.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityKotlinBinding
import com.sq26.experience.util.Log
//import com.squareup.moshi.*
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


fun ComponentActivity.setReceiver(fun1: () -> Unit) {
    val local = "$packageName.local"
    val localBroadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("接收到本地消息")
            fun1()
        }
    }

    val intentFilter = IntentFilter(local)
    var b = false
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            if (b)
                registerReceiver(localBroadcast, intentFilter)
            b = true
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            unregisterReceiver(localBroadcast)

        }
    })
    registerReceiver(localBroadcast, intentFilter)
}

@AndroidEntryPoint
class KotlinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKotlinBinding
    private val viewModel: KotlinViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_kotlin)

        binding.text.text = "moshi"

        setReceiver {
            //在这里做操作也可以把操作写死
            MaterialAlertDialogBuilder(this)
                .setMessage("成功")
                .show()

        }

        binding.text1.setOnClickListener {

            viewModel.start()

        }



    }

}


@HiltViewModel
class KotlinViewModel @Inject constructor(
    private val requestClass: RequestClass
) : ViewModel() {
    fun start() = requestClass.requestError()
}

@Singleton
class RequestClass @Inject constructor(@ApplicationContext private val context: Context) {

    fun requestError() {
        context.sendBroadcast(Intent("${context.packageName}.local").apply {
            //指定包名
            setPackage(context.packageName)
        })
    }

}





