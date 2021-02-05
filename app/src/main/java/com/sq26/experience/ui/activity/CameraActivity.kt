package com.sq26.experience.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.internal.h
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.sq26.experience.BR
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityCameraBinding
import com.sq26.experience.util.Log
import com.sq26.experience.util.kotlin.toast
import com.sq26.experience.util.permissions.JPermissions
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {
    private val cameraViewModel: CameraViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityCameraBinding>(this, R.layout.activity_camera)
            .apply {
                lifecycleOwner = this@CameraActivity
                viewModel = cameraViewModel
                toolbar.setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
                cameraViewModel.imageLiveData.observe(this@CameraActivity) {
                    image.setImageBitmap(it)
                }
            }
        requestPermissions()
    }

    private fun requestPermissions() {
        //申请相机权限
        JPermissions(this, arrayOf(Manifest.permission.CAMERA))
            .success {
                cameraViewModel.isPermission = true
            }.failure { _, _, _ ->
                cameraViewModel.isPermission = false
            }.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 26) {
                data?.apply {
                    cameraViewModel.qrCode =
                        "内容:${getStringExtra("text")}\n格式:${getStringExtra("barcodeFormat")}\n有效位数:${
                            getStringExtra("numBits")
                        }"
                }
            }
        }
    }
}

class CameraViewModel @ViewModelInject constructor(
    @ActivityContext private val context: Context
) : ViewModel(), Observable {
    var isPermission = false

    var imageLiveData = MutableLiveData<Bitmap>()

    @Bindable
    var barcodeFormat = BarcodeFormat.QR_CODE.name

    var text = ""

    @Bindable
    var qrCode = ""
        set(value) {
            field = value
            callbacks.notifyChange(this, BR.qrCode)
        }

    fun scanCode() {
        if (isPermission) {
            (context as AppCompatActivity).startActivityForResult(
                Intent(
                    context,
                    ScanCodeActivity::class.java
                ), 26
            )
        } else {
            context.toast("没有相机权限!")
        }
    }

    fun selectBarcodeFormat() {
        val array = arrayOf(
            BarcodeFormat.CODABAR.name,
            BarcodeFormat.CODE_39.name,
            BarcodeFormat.CODE_93.name,
            BarcodeFormat.CODE_128.name,
            BarcodeFormat.EAN_8.name,
            BarcodeFormat.EAN_13.name,
            BarcodeFormat.ITF.name,
//            BarcodeFormat.RSS_14.name,
//            BarcodeFormat.RSS_EXPANDED.name,
            BarcodeFormat.UPC_A.name,
            BarcodeFormat.UPC_E.name,
//            BarcodeFormat.UPC_EAN_EXTENSION.name,
            BarcodeFormat.AZTEC.name,
            BarcodeFormat.DATA_MATRIX.name,
//            BarcodeFormat.MAXICODE.name,不支持
            BarcodeFormat.PDF_417.name,
            BarcodeFormat.QR_CODE.name
        )
        AlertDialog.Builder(context)
            .setSingleChoiceItems(array, array.indexOf(barcodeFormat)) { _, i ->
                barcodeFormat = array[i]
                callbacks.notifyChange(this, BR.barcodeFormat)
            }
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    fun generateQrCode() {
        if (barcodeFormat == BarcodeFormat.UPC_A.name && text.length != 12) {
            context.toast("UPC_E格式的内容必须为12~13位,最好为12位")
            return
        }
        if (barcodeFormat == BarcodeFormat.UPC_E.name && text.length != 7) {
            context.toast("UPC_E格式的内容必须为7~8位,最好为7位")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {

            } catch (e: Exception) {
                e.printStackTrace()
                context.toast("${barcodeFormat}格式的内容格式错误")
                return@launch
            }
            val bitMatrix =
                MultiFormatWriter().encode(text, BarcodeFormat.valueOf(barcodeFormat), 600, 300)

            val w = bitMatrix.width
            val h = bitMatrix.height
            Log.i(bitMatrix.width.toString(), "w")
            Log.i(bitMatrix.height.toString(), "w")
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    if (bitMatrix[x, y]) {
                        pixels[y * w + x] = -0x1000000
                    } else {
                        pixels[y * w + x] = -0x1
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
            imageLiveData.postValue(bitmap)
        }
    }

    private val callbacks = PropertyChangeRegistry()
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }
}