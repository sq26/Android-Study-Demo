package com.sq26.experience.ui.activity.file

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
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