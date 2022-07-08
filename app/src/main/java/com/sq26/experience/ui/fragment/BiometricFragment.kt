package com.sq26.experience.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.sq26.experience.databinding.FragmentBiometricBinding

class BiometricFragment : Fragment() {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val reg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            Log.i("回调", it.data?.dataString.orEmpty())
        } else {
            Log.i("回调", "异常")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentBiometricBinding.inflate(inflater).apply {

            val biometricManager =
                requireActivity().getSystemService(Context.BIOMETRIC_SERVICE) as BiometricManager

            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS ->
                    t1.text = "应用程序可以使用生物特征进行身份验证。"
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                    t1.text = "此设备上没有可用的生物特征。"
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                    t1.text = "生物特征目前不可用。"
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    // 提示用户创建应用程序接受的凭据。
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BiometricManager.Authenticators.BIOMETRIC_STRONG
                        )
                    }
                    reg.launch(enrollIntent)
                }
            }

            var count = 0
            fun initBiometric() {
                biometricPrompt =
                    BiometricPrompt(this@BiometricFragment,
                        ContextCompat.getMainExecutor(requireActivity()),
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationError(
                                errorCode: Int,
                                errString: CharSequence
                            ) {
                                super.onAuthenticationError(errorCode, errString)
                                t2.text = "身份验证错误: $errString"
                            }

                            override fun onAuthenticationSucceeded(
                                result: BiometricPrompt.AuthenticationResult
                            ) {
                                super.onAuthenticationSucceeded(result)
                                t2.text = "身份验证成功！"
                            }

                            override fun onAuthenticationFailed() {
                                super.onAuthenticationFailed()
                                count += 1
                                t2.text = "身份验证失败！$count 次"
                            }
                        })
            }
            initBiometric()
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("生物识别登录")
                .setSubtitle("使用生物识别凭据登录")
                .setNegativeButtonText("使用帐户密码")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build()

            button.setOnClickListener {
                biometricPrompt.authenticate(promptInfo)
            }
            button2.setOnClickListener {
                initBiometric()
            }
        }.root
    }
}