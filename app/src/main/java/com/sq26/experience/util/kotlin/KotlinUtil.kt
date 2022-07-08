package com.sq26.experience.util.kotlin

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.sq26.experience.R
import java.text.DecimalFormat
import kotlin.reflect.KClass

//往Context中加入Toast,isLong表示是否是长时间显示
fun Context.toast(text: CharSequence, isLong: Boolean = false) =
    Toast.makeText(this, text, if (!isLong) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()

//往Context中加入简单文本弹出框,isDetermine表示是否有确定按钮
fun Context.dialog(text: CharSequence, isDetermine: Boolean = false) {
    val alertDialog: AlertDialog = AlertDialog.Builder(this)
        .setMessage(text)
        .create()
    if (isDetermine)
        alertDialog.setButton(
            DialogInterface.BUTTON_POSITIVE,
            getText(R.string.determine)
        ) { _, _ ->
            alertDialog.dismiss()
        }
    alertDialog.show()
}

//计算文件大小并加上相应单位
fun Long.getFileSizeStr(): String {
    val df = DecimalFormat("0.00") //设置保留位数
    //Android7.0以上是单位计算改为1000进一
    val d: Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        1000
    } else {
        1024
    }
    //获取位数长度
    val ws: Int = this.toString().length
    return when {
        ws > 12 -> {
            df.format((this.toFloat() / (d * d * d * d)).toDouble()) + "TB"
        }
        ws > 9 -> {
            df.format((this.toFloat() / (d * d * d)).toDouble()) + "GB"
        }
        ws > 6 -> {
            df.format((this.toFloat() / (d * d)).toDouble()) + "MB"
        }
        ws > 3 -> {
            df.format((this.toFloat() / d).toDouble()) + "KB"
        }
        else -> {
            this.toString() + "B"
        }
    }
}
/**
 * dp转px
 * 使用方法context.resources.displayMetrics.dpToPx(dp)
 */
fun DisplayMetrics.dpToPx(dp: Int): Int {
    return (density * dp + 0.5f).toInt()
}


@MainThread
public inline fun <reified VM : ViewModel> ComponentActivity.applicationViewModel(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }

    return ApplicationViewModelLazy(VM::class, { viewModelStore }, factoryPromise)
}

public class ApplicationViewModelLazy<VM : ViewModel>(
    private val viewModelClass: KClass<VM>,
    private val storeProducer: () -> ViewModelStore,
    private val factoryProducer: () -> ViewModelProvider.Factory
) : Lazy<VM> {
    private var cached: VM? = null

    override val value: VM
        get() {
            val viewModel = cached
            return if (viewModel == null) {
                val factory = factoryProducer()
                val store = storeProducer()
                ViewModelProvider(store, factory).get(viewModelClass.java).also {
                    cached = it
                }
            } else {
                viewModel
            }
        }

    override fun isInitialized(): Boolean = cached != null
}