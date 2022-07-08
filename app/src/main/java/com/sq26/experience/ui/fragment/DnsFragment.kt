package com.sq26.experience.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.sq26.experience.databinding.FragmentDnsBinding
import com.sq26.experience.util.setOnClickAntiShake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException
import java.net.InetAddress


class DnsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentDnsBinding.inflate(inflater).apply {

            domain.setText("topmed.net.cn")

            button.setOnClickAntiShake {

                lifecycleScope.launch(Dispatchers.IO) {
                    val inetAddressArray = InetAddress.getAllByName(domain.text.toString())
                    var msg = ""
                    for (inetAddress in inetAddressArray) {
                        msg += inetAddress.hostAddress?.plus("\n") ?: ""
                    }
                    withContext(Dispatchers.Main) {
                        text1.text = "系统Dns\n$msg"
                    }
                }
                val params = "name=${domain.text.toString()}&type=1"
                val okHttpClient = OkHttpClient.Builder().build()
                val call = okHttpClient.newCall(
                    Request.Builder()
                        .url("https://dns.alidns.com/resolve?$params")
                        .build()
                )
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            text2.text = "阿里Dns\n" + e.message
                        }
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val msg = response.body?.string()
                        lifecycleScope.launch(Dispatchers.Main) {
                            text2.text = "阿里Dns\n$msg"
                        }
                        Log.i("msg", msg.orEmpty())
                    }
                })
                val call2 = okHttpClient.newCall(
                    Request.Builder()
                        .url("https://doh.pub/dns-query?$params")
                        .build()
                )
                call2.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            text3.text = "腾讯Dns\n" + e.message
                        }
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val msg = response.body?.string()
                        lifecycleScope.launch(Dispatchers.Main) {
                            text3.text = "腾讯Dns\n$msg"
                        }
                        Log.i("msg", msg.orEmpty())
                    }
                })

            }

        }.root
    }
}