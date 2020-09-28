package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sq26.experience.R
import kotlinx.android.synthetic.main.activity_kotlin.*

class KotlinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
        text.text = "123"
    }
}