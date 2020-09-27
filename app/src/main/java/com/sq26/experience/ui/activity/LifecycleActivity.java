package com.sq26.experience.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import android.os.Bundle;
import android.util.Log;

import com.sq26.experience.R;

public class LifecycleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle);
        //监听生命周期
        //方式1(注解方式实现)(不被官方推荐)
        getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            public void onCreate() {
                Log.i("1", "onCreate");
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            public void onStart() {
                Log.i("1", "onStart");
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            public void onResume() {
                Log.i("1", "onResume");
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            public void onPause() {
                Log.i("1", "onPause");
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            public void onStop() {
                Log.i("1", "onStop");
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            public void onDestroy() {
                Log.i("1", "onDestroy");
            }
        });
        //方式二,继承DefaultLifecycleObserver实现(官方推荐)
        getLifecycle().addObserver(new MainPresenter());
    }

    static class MainPresenter implements DefaultLifecycleObserver {

        @Override
        public void onCreate(@NonNull LifecycleOwner owner) {
            Log.i("2", "onCreate");
        }

        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            Log.i("2", "onStart");
        }

        @Override
        public void onResume(@NonNull LifecycleOwner owner) {
            Log.i("2", "onResume");
        }

        @Override
        public void onPause(@NonNull LifecycleOwner owner) {
            Log.i("2", "onPause");
        }

        @Override
        public void onStop(@NonNull LifecycleOwner owner) {
            Log.i("2", "onStop");
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            Log.i("2", "onDestroy");
        }
    }
}