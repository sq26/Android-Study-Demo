package com.sq26.experience.ui.activity.file

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityFileImageBinding
import com.sq26.experience.util.permissions.JPermissions
import com.sq26.experience.viewmodel.FileImageViewModel

class FileImageActivity : AppCompatActivity() {
    private val viewModel: FileImageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.translucent, theme)
        window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.translucent, theme)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            WindowCompat.
//            window.insetsController?.let {
//                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
//            }
        } else {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            WindowCompat.
        }
        DataBindingUtil.setContentView<ActivityFileImageBinding>(this, R.layout.activity_file_image)



        JPermissions(this).success {
            viewModel.initData(this)
        }.start(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }
}