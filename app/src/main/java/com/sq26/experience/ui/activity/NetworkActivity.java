package com.sq26.experience.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sq26.experience.R;
import com.sq26.experience.util.network.download.Download;

public class NetworkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        Download.initialize(this,"http://192.168.8.210:8080/tomcat.png").start();

    }
}
