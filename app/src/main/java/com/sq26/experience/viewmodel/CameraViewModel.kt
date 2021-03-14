package com.sq26.experience.viewmodel

import android.graphics.Bitmap
import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.sq26.experience.BR
import com.sq26.experience.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CameraViewModel @Inject constructor() : BaseViewModel() {
    var isPermission = false

    var imageLiveData = MutableLiveData<Bitmap>()

    @Bindable
    var barcodeFormat = BarcodeFormat.QR_CODE.name
        set(value) {
            field = value
            notifyPropertyChanged(BR.barcodeFormat)
        }

    var text = ""

    @Bindable
    var qrCode = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.qrCode)
        }

    var startScanCodeActivity: () -> Unit = {}

    fun scanCode() {
        if (isPermission) {
            startScanCodeActivity()
        } else {
            toast.postValue("没有相机权限!")
        }
    }

    var selectBarcodeFormatDialog: (Array<String>, Int) -> Unit = { _, _ -> }

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

        selectBarcodeFormatDialog(array,array.indexOf(barcodeFormat))
    }

    fun generateQrCode() {
        if (barcodeFormat == BarcodeFormat.UPC_A.name && text.length != 12) {
            toast.postValue("UPC_E格式的内容必须为12~13位,最好为12位!")
            return
        }
        if (barcodeFormat == BarcodeFormat.UPC_E.name && text.length != 7) {
            toast.postValue("UPC_E格式的内容必须为7~8位,最好为7位!")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {

            } catch (e: Exception) {
                e.printStackTrace()
                toast.postValue("${barcodeFormat}格式的内容格式错误")
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

}