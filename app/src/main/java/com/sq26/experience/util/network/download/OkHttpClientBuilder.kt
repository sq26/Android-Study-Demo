package com.sq26.experience.util.network.download

import okhttp3.OkHttpClient

class OkHttpClientBuilder {
    companion object {
        private var instance: OkHttpClient? = null
        fun getInstance(): OkHttpClient {
            if (instance == null)
                instance = OkHttpClient.Builder().build()
            return instance!!
        }
    }
}