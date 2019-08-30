package com.sq26.experience.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.sq26.experience.R;

import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        long time = 1489239412;
        long ling = 1000;

        long start = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < 2000; i++) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("time", time);
            jsonObject1.put("ling", ling);
            jsonObject.put("" + i, jsonObject1);
        }
        long end = System.currentTimeMillis();
        Log.d("JSONObject写速度", (end - start) + "");

        long start2 = System.currentTimeMillis();
        Map<String, Map> map = new HashMap<>();
        for (int i = 0; i < 2000; i++) {
            Map<String, Long> map2 = new HashMap<>();
            map2.put("time", time);
            map2.put("ling", ling);
            map.put("" + i, map2);
        }
        long end2 = System.currentTimeMillis();
        Log.d("HashMap写速度", (end2 - start2) + "");

        long start3 = System.currentTimeMillis();
        for (int i = 0; i < jsonObject.size(); i++) {
            jsonObject.getJSONObject("" + i).getInteger("");
        }
        long end3 = System.currentTimeMillis();
        Log.d("JSONObject读速度", (end3 - start3) + "");

        long start4 = System.currentTimeMillis();
        for (int i = 0; i < map.size(); i++) {
            map.get("" + i).get("ling");
        }
        long end4 = System.currentTimeMillis();
        Log.d("HashMap读速度", (end4 - start4) + "");


    }
}
