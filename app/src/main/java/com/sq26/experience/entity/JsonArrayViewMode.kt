package com.sq26.experience.entity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alibaba.fastjson.JSONArray
import com.sq26.experience.util.Log

//视图本身是运行在主线程的
class JsonArrayViewMode : ViewModel() {
    private var jsonArrayMutableLiveData: MutableLiveData<JSONArray>? = null

    //获取JSONArray并初始化数据,如果初始化过就直接返回数据
    val jsonArrayLiveData: LiveData<JSONArray>
        get() {
            //判断是否初始化过
            if (jsonArrayMutableLiveData == null) {
                //初始化数据
                jsonArrayMutableLiveData = MutableLiveData<JSONArray>()
                initData()
            }
            //返回
            return jsonArrayMutableLiveData!!
        }

    private fun initData() {
        val jsonArray = JSONArray()
        Thread {
            for (i in 0..99) {
                jsonArray[0] = "" + i
                Log.i(i.toString())
                if (jsonArrayMutableLiveData!!.hasObservers())
                    jsonArrayMutableLiveData!!.postValue(jsonArray)
                else
                    break
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        //用于被销毁时清除数据源
        Log.i("onCleared", "销毁ViewModel")
    }
}