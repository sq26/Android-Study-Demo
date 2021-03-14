package com.sq26.experience.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityCameraBinding
import com.sq26.experience.util.permissions.JPermissions
import com.sq26.experience.viewmodel.CameraViewModel
import com.sq26.experience.viewmodel.setBaseViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {
    //获取viewModel
    private val cameraViewModel: CameraViewModel by viewModels()

    //注册跳转扫码页面的返回操作
    private val requestScanCode =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.apply {
                //设置返回的内容
                cameraViewModel.qrCode =
                    "内容:${getStringExtra("text")}\n格式:${getStringExtra("barcodeFormat")}\n有效位数:${
                        getStringExtra("numBits")
                    }"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBaseViewModel(cameraViewModel)
        DataBindingUtil.setContentView<ActivityCameraBinding>(this, R.layout.activity_camera)
            .apply {
                lifecycleOwner = this@CameraActivity
                viewModel = cameraViewModel

                toolbar.setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
                //设置二维码一维码图像
                cameraViewModel.imageLiveData.observe(this@CameraActivity) {
                    image.setImageBitmap(it)
                }
            }
        requestPermissions()
        //给viewModel设置跳转页面方法
        cameraViewModel.startScanCodeActivity = {
            requestScanCode.launch(Intent(this, ScanCodeActivity::class.java))
        }
        //设置选择类型的方法
        cameraViewModel.selectBarcodeFormatDialog = { array,int->
            MaterialAlertDialogBuilder(this)
                .setSingleChoiceItems(array,int){ _, i ->
                    cameraViewModel.barcodeFormat = array[i]
                }
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
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

}
