package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityKotlinBinding

class KotlinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKotlinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKotlinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.text.text = "123"
    }
}