package com.sq26.experience.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * 一个可以感知activity和fragment生命周期的子线程工具类
 * 可以在生命周期进入onDestroy时自动修改线程的中止标识
 * 子线程的循环业务中监听到终止标识,自动结束子线程或是处理终止数据
 */
public class LifecycleThread implements DefaultLifecycleObserver {
    //创建一个线程
    private Thread thread;

    public LifecycleThread(Context context, Runnable runnable) {
        //初始化线程
        thread = new Thread(runnable);
        //绑定生命周期
        ((AppCompatActivity) context).getLifecycle().addObserver(this);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        //监听结束周期
        //设置线程中止标识
        thread.interrupt();
    }

    public void start() {
        //启动线程
        thread.start();
    }
}
