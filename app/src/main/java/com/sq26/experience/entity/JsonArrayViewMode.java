package com.sq26.experience.entity;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alibaba.fastjson.JSONArray;
//视图本身是运行在主线程的
public class JsonArrayViewMode extends ViewModel {

    private MutableLiveData<JSONArray> jsonArrayMutableLiveData;
    //获取JSONArray并初始化数据,如果初始化过就直接返回数据
    public LiveData<JSONArray> getJsonArray() {
        //判断是否为空
        if (jsonArrayMutableLiveData == null) {
            jsonArrayMutableLiveData = new MutableLiveData<>();
            //是空,说明是第一次创建,在这里初始化数据
            initData();
        }
        return jsonArrayMutableLiveData;
    }

    private void initData() {
        JSONArray jsonArray = new JSONArray();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0;i<100;i++){
                    jsonArray.set(0,""+i);
                    Log.i("2",i+"i");
                    jsonArrayMutableLiveData.postValue(jsonArray);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        //用于被销毁时清除数据源
        Log.i("onCleared", "销毁ViewModel");
    }
}
