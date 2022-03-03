package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityStorageManagementBinding
import com.sq26.experience.util.setOnClickAntiShake

/**
 * 自定义储存管理页
 */
class StorageManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityStorageManagementBinding>(
            this,
            R.layout.activity_storage_management
        ).apply {
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            button.setOnClickAntiShake {
                if (allData.isChecked) {
                    filesDir.delete()
                    getExternalFilesDir(null)?.delete()
                }
            }
        }
    }
}