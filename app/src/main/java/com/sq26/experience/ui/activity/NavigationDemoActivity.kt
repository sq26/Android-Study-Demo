package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.navArgs
import com.sq26.experience.databinding.ActivityNavigationDemoBinding

class NavigationDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNavigationDemoBinding
    private val args: NavigationDemoActivityArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            text.text = args.index.toString()
        }
    }
}