package com.sq26.experience.util.kotlin

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.sq26.experience.R

//往Context中加入Toast,isLong表示是否是长时间显示
fun Context.toast(text: CharSequence, isLong: Boolean = false) =
        Toast.makeText(this, text, if (!isLong) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()

//往Context中加入简单文本弹出框,isDetermine表示是否有确定按钮
fun Context.dialog(text: CharSequence, isDetermine: Boolean = false) {
    val alertDialog: AlertDialog = AlertDialog.Builder(this)
            .setMessage(text)
            .create()
    if (isDetermine)
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getText(R.string.determine)) { _, _ ->
            alertDialog.dismiss()
        }
    alertDialog.show()
}
