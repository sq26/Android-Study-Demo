package com.sq26.experience.ui.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sq26.experience.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }
}