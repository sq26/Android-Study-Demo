package com.sq26.experience.ui.activity

import android.content.Intent
import android.graphics.ImageFormat
import android.os.Bundle
import android.util.Size
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityScanCodeBinding
import com.sq26.experience.util.Log
import java.lang.Exception
import java.util.concurrent.Executors

class ScanCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityScanCodeBinding>(this, R.layout.activity_scan_code)
            .let {
                it.toolbar.setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }

                //要解析的图片编码编码格式(默认QR_CODE)
                val barcodeFormats = mutableListOf(BarcodeFormat.QR_CODE)
                //声明多个格式读取器
                val reader = MultiFormatReader().apply {
                    //设置格式识别设置
                    setHints(mapOf(Pair(DecodeHintType.POSSIBLE_FORMATS, barcodeFormats)))
                }
                it.toolbar.menu.add("条形码").apply {
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                    setOnMenuItemClickListener {

                        val barcodeFormatNames = arrayOf(
                            BarcodeFormat.CODABAR.name,
                            BarcodeFormat.CODE_39.name,
                            BarcodeFormat.CODE_93.name,
                            BarcodeFormat.CODE_128.name,
                            BarcodeFormat.EAN_8.name,
                            BarcodeFormat.EAN_13.name,
                            BarcodeFormat.ITF.name,
                            BarcodeFormat.RSS_14.name,
                            BarcodeFormat.RSS_EXPANDED.name,
                            BarcodeFormat.UPC_A.name,
                            BarcodeFormat.UPC_E.name,
                            BarcodeFormat.UPC_EAN_EXTENSION.name
                        )
                        val checkedItems = booleanArrayOf(
                            barcodeFormats.contains(BarcodeFormat.CODABAR),
                            barcodeFormats.contains(BarcodeFormat.CODE_39),
                            barcodeFormats.contains(BarcodeFormat.CODE_93),
                            barcodeFormats.contains(BarcodeFormat.CODE_128),
                            barcodeFormats.contains(BarcodeFormat.EAN_8),
                            barcodeFormats.contains(BarcodeFormat.EAN_13),
                            barcodeFormats.contains(BarcodeFormat.ITF),
                            barcodeFormats.contains(BarcodeFormat.RSS_14),
                            barcodeFormats.contains(BarcodeFormat.RSS_EXPANDED),
                            barcodeFormats.contains(BarcodeFormat.UPC_A),
                            barcodeFormats.contains(BarcodeFormat.UPC_E),
                            barcodeFormats.contains(BarcodeFormat.UPC_EAN_EXTENSION)
                        )
                        AlertDialog.Builder(this@ScanCodeActivity)
                            .setMultiChoiceItems(
                                barcodeFormatNames,
                                checkedItems
                            ) { _, which, isChecked ->
                                if (isChecked) {
                                    if (!barcodeFormats.contains(
                                            BarcodeFormat.valueOf(
                                                barcodeFormatNames[which]
                                            )
                                        )
                                    )
                                        barcodeFormats.add(BarcodeFormat.valueOf(barcodeFormatNames[which]))
                                } else {
                                    barcodeFormats.remove(BarcodeFormat.valueOf(barcodeFormatNames[which]))
                                }
                                reader.setHints(
                                    mapOf(
                                        Pair(
                                            DecodeHintType.POSSIBLE_FORMATS,
                                            barcodeFormats
                                        )
                                    )
                                )
                            }
                            .show()
                        true
                    }
                }
                it.toolbar.menu.add("二维码").apply {
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                    setOnMenuItemClickListener {
                        val barcodeFormatNames = arrayOf(
                            BarcodeFormat.AZTEC.name,
                            BarcodeFormat.DATA_MATRIX.name,
                            BarcodeFormat.MAXICODE.name,
                            BarcodeFormat.PDF_417.name,
                            BarcodeFormat.QR_CODE.name
                        )
                        val checkedItems = booleanArrayOf(
                            barcodeFormats.contains(BarcodeFormat.AZTEC),
                            barcodeFormats.contains(BarcodeFormat.DATA_MATRIX),
                            barcodeFormats.contains(BarcodeFormat.MAXICODE),
                            barcodeFormats.contains(BarcodeFormat.PDF_417),
                            barcodeFormats.contains(BarcodeFormat.QR_CODE)
                        )
                        AlertDialog.Builder(this@ScanCodeActivity)
                            .setMultiChoiceItems(
                                barcodeFormatNames,
                                checkedItems
                            ) { _, which, isChecked ->
                                if (isChecked) {
                                    if (!barcodeFormats.contains(
                                            BarcodeFormat.valueOf(
                                                barcodeFormatNames[which]
                                            )
                                        )
                                    )
                                        barcodeFormats.add(BarcodeFormat.valueOf(barcodeFormatNames[which]))
                                } else {
                                    barcodeFormats.remove(BarcodeFormat.valueOf(barcodeFormatNames[which]))
                                }
                                reader.setHints(
                                    mapOf(
                                        Pair(
                                            DecodeHintType.POSSIBLE_FORMATS,
                                            barcodeFormats
                                        )
                                    )
                                )
                            }
                            .show()
                        true
                    }
                }
                //获取相机服务
                val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
                //初始化相机,添加相机回调
                cameraProviderFuture.addListener(Runnable {
                    //获取相机提供者
                    val cameraProvider = cameraProviderFuture.get()
                    //获取预览
                    val preview: Preview = Preview.Builder()
                        .build()
                    //设置预览的显示提供者
                    preview.setSurfaceProvider(it.previewView.surfaceProvider)
                    //指定所需相机镜头,设置后置摄像头
                    val cameraSelector: CameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    //创建图像分析
                    val imageAnalysis = ImageAnalysis.Builder()
                        //设置预期的分辨率,如果不存在会自动采取最相近的分辨率数值
                        .setTargetResolution(Size(640, 640))
                        //设置非阻塞模式,会从相机接收最新的可用帧。
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                    //创建一个缓存线程池
                    val executor = Executors.newCachedThreadPool()
                    //设置分析处理
                    imageAnalysis.setAnalyzer(executor) {
                        //判断图需格式是否YUV_420_888
                        if (it.format == ImageFormat.YUV_420_888) {
                            //获取一组图片的第一张的byteBuffer
                            val buffer = it.planes[0].buffer
                            //根据buffer的元素数创建字节数组
                            val data = ByteArray(buffer.remaining())
                            //将缓存区输出到指定数组
                            buffer.get(data)
                            val width = it.width
                            val height = it.height
                            Log.i(width.toString(), "width")
                            Log.i(height.toString(), "height")
                            //获取到可用于zxing解析的数据源
                            val source = PlanarYUVLuminanceSource(
                                data,
                                width,
                                height,
                                0,
                                0,
                                width,
                                height,
                                false
                            )
                            //将数据源封装成zxing解析格式,在转化成位整列bitmap
                            val bitmap = BinaryBitmap(HybridBinarizer(source))
                            //可能出现异常
                            try {
                                //由读取器进行解码
                                val result = reader.decodeWithState(bitmap)
                                //得到的结果返回调用页面
                                setResult(RESULT_OK, Intent().apply {
                                    //条形码编码的原始文本
                                    putExtra("text", result.text)
                                    //条形码的格式
                                    putExtra("barcodeFormat", result.barcodeFormat.name)
                                    //时间戳
                                    putExtra("numBits", result.numBits.toString())
                                })
                                finish()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        //设置image.close()关闭图像引用,以避免阻塞其他图像的生成（导致预览停顿）并避免可能出现的图像丢失。
                        it.close()
                    }
                    //给相机提供者绑定声明周期并设置相机设置,分析对象和预览对象
                    cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)

                }, ContextCompat.getMainExecutor(this))
            }
    }
}