package com.sq26.experience.ui.activity

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.lifecycleScope
import com.sq26.experience.databinding.ActivityPagingBinding
import kotlinx.coroutines.launch

class PagingActivity : AppCompatActivity() {
    //lateinit标识延迟初始化
    private lateinit var binding: ActivityPagingBinding
    private val viewModel by viewModels<BaseViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {

        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
    }
}

class BaseViewModel(application: Application) : AndroidViewModel(application){

}