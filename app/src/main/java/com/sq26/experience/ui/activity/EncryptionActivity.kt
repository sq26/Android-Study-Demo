package com.sq26.experience.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel
import com.sq26.experience.BR
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityEncryptionBinding
import com.sq26.experience.util.Encrypt
import com.sq26.experience.viewmodel.BaseViewModel
import com.sq26.experience.viewmodel.ObservableViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import javax.crypto.Cipher
import javax.inject.Inject

@AndroidEntryPoint
class EncryptionActivity : AppCompatActivity() {
    private val encryptionViewModel: EncryptionViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityEncryptionBinding>(
            this,
            R.layout.activity_encryption
        ).apply {
            lifecycleOwner = this@EncryptionActivity
            viewModel = encryptionViewModel
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}

@HiltViewModel
class EncryptionViewModel @Inject constructor(
) : ObservableViewModel() {
    //公用密钥
    @Bindable
    var publicKey = ""

    //私有密钥
    @Bindable
    var privateKey = ""

    //是否显示私有密钥
    @Bindable
    var privateKeyVisibility = View.GONE

    //明文
    @Bindable
    var plaintext = ""

    //密文
    @Bindable
    var ciphertext = ""

    //加密算法
    @Bindable
    var algorithm = "算法"

    //加密模式
    @Bindable
    var mode = "模式"

    //填充模式
    @Bindable
    var padding = "填充"

    //用时
    @Bindable
    var timing = ""

    //设置加密算法
    fun onAlgorithm(view: View) {
        val item = arrayOf(
            "AES",
            "AES_128",
            "AES_256",
            "ARC4",
            "BLOWFISH",
            "BLOWFISH",
            "ChaCha20",
            "DES",
            "DESede",
            "RSA"
        )
        AlertDialog.Builder(view.context)
            .setItems(item) { _, i ->
                algorithm = item[i]
                notifyPropertyChanged(BR.algorithm)
                privateKeyVisibility = if (algorithm == "RSA") View.VISIBLE else View.GONE
                notifyPropertyChanged(BR.privateKeyVisibility)
            }.show()
    }

    //设置加密模式
    fun onMode(view: View) {
        val item = arrayOf(
            "CBC",
            "CFB",
            "CTR",
            "CTS",
            "ECB",
            "OFB",
            "GCM",
            "NONE",
            "Poly1305"
        )
        AlertDialog.Builder(view.context)
            .setItems(item) { _, i ->
                mode = item[i]
                notifyPropertyChanged(BR.mode)
            }.show()
    }

    //设置填充模式
    fun onPadding(view: View) {
        val item = arrayOf(
            "ISO10126Padding",
            "NoPadding",
            "PKCS5Padding",
            "OAEPPadding",
            "PKCS1Padding",
            "OAEPwithSHA-1andMGF1Padding",
            "OAEPwithSHA-256andMGF1Padding",
            "OAEPwithSHA-224andMGF1Padding",
            "OAEPwithSHA-384andMGF1Padding",
            "OAEPwithSHA-512andMGF1Padding"
        )
        AlertDialog.Builder(view.context)
            .setItems(item) { _, i ->
                padding = item[i]
                notifyPropertyChanged(BR.padding)
            }.show()
    }
    //生成密钥

    fun onGenerateKey(view: View) {
        val item = arrayOf(
            "AES",
            "DES",
            "DESede",
            "HmacSHA1",
            "HmacSHA224",
            "HmacSHA256",
            "HmacSHA384",
            "HmacSHA512",
            "PBEwithHmacSHA1",
            "PBEwithHmacSHA1AndAES_128",
            "PBEwithHmacSHA1AndAES_256",
            "PBEwithHmacSHA224AndAES_128",
            "PBEwithHmacSHA224AndAES_256",
            "PBEwithHmacSHA256AndAES_128",
            "PBEwithHmacSHA256AndAES_256",
            "PBEwithHmacSHA384AndAES_128",
            "PBEwithHmacSHA384AndAES_256",
            "PBEwithHmacSHA512AndAES_128",
            "PBEwithHmacSHA512AndAES_256",
            "PBEwithMD5AND128BITAES-CBC-OPENSSL",
            "PBEwithMD5AND192BITAES-CBC-OPENSSL",
            "PBEwithMD5AND256BITAES-CBC-OPENSSL",
            "PBEwithMD5ANDDES",
            "PBEwithMD5ANDRC2",
            "PBEwithSHA1ANDDES",
            "PBEwithSHA1ANDRC2",
            "PBEwithSHA256AND128BITAES-CBC-BC",
            "PBEwithSHA256AND192BITAES-CBC-BC",
            "PBEwithSHA256AND256BITAES-CBC-BC",
            "PBEwithSHAAND128BITAES-CBC-BC",
            "PBEwithSHAAND128BITRC2-CBC",
            "PBEwithSHAAND128BITRC4",
            "PBEwithSHAAND192BITAES-CBC-BC",
            "PBEwithSHAAND2-KEYTRIPLEDES-CBC",
            "PBEwithSHAAND256BITAES-CBC-BC",
            "PBEwithSHAAND3-KEYTRIPLEDES-CBC",
            "PBEwithSHAAND40BITRC2-CBC",
            "PBEwithSHAAND40BITRC4",
            "PBEwithSHAANDTWOFISH-CBC",
            "PBKDF2withHmacSHA1",
            "PBKDF2withHmacSHA1And8BIT",
            "PBKDF2withHmacSHA224",
            "PBKDF2withHmacSHA256",
            "PBKDF2withHmacSHA384",
            "PBKDF2withHmacSHA512",
        )
        AlertDialog.Builder(view.context)
            .setItems(item) { _, i ->
                publicKey = Base64.encodeToString(
                    Encrypt.getRawKey(
                        1000,
                        256,
                        item[i],
                        "123456"
                    ), Base64.DEFAULT
                )
                notifyPropertyChanged(BR.publicKey)
            }.show()

    }

    fun onGenerateAsymmetricKeys() {
        val keyPair = Encrypt.getRSAKeyPair(1025)
        publicKey = Base64.encodeToString(Encrypt.getRSAPublicKey(keyPair), Base64.DEFAULT)
        notifyPropertyChanged(BR.publicKey)
        privateKey = Base64.encodeToString(Encrypt.getRSAPrivateKey(keyPair), Base64.DEFAULT)
        notifyPropertyChanged(BR.privateKey)
    }

    //加密
    fun encryption() {
        val time = System.currentTimeMillis()
        Encrypt()
            .Algorithm(algorithm)
            .Key(Base64.decode(publicKey, Base64.DEFAULT))
            .Modes(mode)
            .Paddings(padding)
            .isPublicKey(true)
            .Plaintext(plaintext.toByteArray())
            .setOpmode(Cipher.ENCRYPT_MODE)
            .setOnComplete {
                ciphertext = Base64.encodeToString(it, Base64.DEFAULT)
                notifyPropertyChanged(BR.ciphertext)
                timing = (System.currentTimeMillis() - time).toString()
                notifyPropertyChanged(BR.timing)
            }.start()
    }

    //解密
    fun decrypt() {
        val time = System.currentTimeMillis()
        Encrypt()
            .Algorithm(algorithm)
            .Key(
                if (algorithm == "RSA") Base64.decode(
                    privateKey,
                    Base64.DEFAULT
                ) else Base64.decode(publicKey, Base64.DEFAULT)
            )
            .Modes(mode)
            .Paddings(padding)
            .isPublicKey(algorithm != "RSA")
            .Plaintext(
                Base64.decode(ciphertext.toByteArray(), Base64.DEFAULT)
            )
            .setOpmode(Cipher.DECRYPT_MODE)
            .setOnComplete {
                plaintext = String(it)
                notifyPropertyChanged(BR.plaintext)
                timing = (System.currentTimeMillis() - time).toString()
                notifyPropertyChanged(BR.timing)
            }.start()
    }

}