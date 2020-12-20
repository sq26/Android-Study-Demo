package com.sq26.experience.util.network.download

import android.annotation.SuppressLint
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class OkHttpClientBuilder {
    companion object {
        @Volatile
        private var instance: OkHttpClient? = null
        private val VERIFY_HOST_NAME_ARRAY: Array<String> = arrayOf()
        fun getInstance(): OkHttpClient {
            if (instance == null) {
                val trustAllCerts: Array<X509TrustManager> = arrayOf(object : X509TrustManager {
                    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                    }

                    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                })
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustAllCerts, SecureRandom())
                instance = OkHttpClient.Builder()
                    //忽略host验证
                    .hostnameVerifier(object : HostnameVerifier {
                        override fun verify(p0: String?, p1: SSLSession?): Boolean {
                            if (p0.isNullOrEmpty())
                                return false
                            return !VERIFY_HOST_NAME_ARRAY.contains(p0)
                        }
                    })
                    .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0])
                    .build()
            }
            return instance!!
        }
    }
}