package com.sq26.experience.app

import okhttp3.*
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class OkHttpUtil(private val url: String) {
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
                    //连接超时时间20秒
                    .connectTimeout(20, TimeUnit.SECONDS)
                    //写入超时时间
                    .writeTimeout(20, TimeUnit.SECONDS)
                    //通讯超时时间20秒
                    .callTimeout(20, TimeUnit.SECONDS)
                    //读取超时30秒
                    .readTimeout(20, TimeUnit.SECONDS)
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