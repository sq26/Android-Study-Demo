package com.sq26.experience.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.sq26.experience.R;
import com.sq26.experience.util.LifecycleThread;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveDataActivity extends AppCompatActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);
        ButterKnife.bind(this);
        //设置进度条最大值100
        progressBar.setMax(100);
        //设置文本1默认值1
        text1.setText("1");
        //设置文本2默认值2
        text2.setText("2");
        //创建进度条数据源类型
        MutableLiveData<Integer> LiveDataProgress = new MutableLiveData<>();
        //设置进度条数据源监听
        LiveDataProgress.observe(this, integer -> progressBar.setProgress(integer));
        //可以添加多个监听回调
        LiveDataProgress.observe(this, integer -> Log.i("1", integer + ""));
        //创建进度条缓冲条数据源类型
        MutableLiveData<Integer> LiveDataSecondaryProgress = new MutableLiveData<>();
        //设置进度条缓冲条数据源监听
        LiveDataSecondaryProgress.observe(this, integer -> progressBar.setSecondaryProgress(integer));

        //创建中介者LiveData,可以监听另一个LiveData的数据变化
        MediatorLiveData<Integer> mediatorLiveData = new MediatorLiveData<>();
        //添加对LiveDataProgress的监听
        mediatorLiveData.addSource(LiveDataProgress, integer -> {
            text1.setText(integer + "");
        });
        //同时也可以做为一个liveData，被其他Observer观察。
        mediatorLiveData.observe(this, integer -> text2.setText(integer + ""));
        //在子线程中刷新进度条数据
        new LifecycleThread(this, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++) {
                    //判断是否标识终止
                    if (Thread.currentThread().isInterrupted())
                        //是标识终止,就return结束run()方法
                        return;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        //这里的异常多半是使用interrupt()方法引起的,
                        //直接结束run()方法
                        return;
                    }
                    Log.i("Thread", i + "");
                    //更新数据(setValue()方法之能在主线程中使用)
                    LiveDataProgress.postValue(i);
                }
            }
        }).start();

        //在子线程中刷新进度条缓冲值数据
        //在子线程中刷新中介者的数据
        new LifecycleThread(this, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++) {
                    if (Thread.currentThread().isInterrupted())
                        return;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        return;
                    }
                    LiveDataSecondaryProgress.postValue(i);
                    mediatorLiveData.postValue(i);
                }
            }
        }).start();

    }
}