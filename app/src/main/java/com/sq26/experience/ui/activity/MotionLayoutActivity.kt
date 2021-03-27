package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityMotionLayoutBinding
import com.sq26.experience.util.Log

class MotionLayoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMotionLayoutBinding>(
            this,
            R.layout.activity_motion_layout
        ).apply {


        }
    }
}