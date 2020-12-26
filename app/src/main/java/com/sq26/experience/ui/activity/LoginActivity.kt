package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private val args: LoginActivityArgs by navArgs()
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.text.text = args.text
        binding.text.setOnClickListener {
            finish()
        }
    }
}