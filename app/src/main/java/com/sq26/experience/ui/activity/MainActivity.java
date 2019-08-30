package com.sq26.experience.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sq26.experience.R;
import com.sq26.experience.adapter.RecyclerViewAdapter;
import com.sq26.experience.adapter.RecyclerViewJsonArrayAdapter;
import com.sq26.experience.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        init();
    }

    private void init() {
        JSONArray jsonArray = new JSONArray();

        jsonArray.add(initItem("", "java技术", 0));
        jsonArray.add(initItem("rxjava", "RXjava的使用", 1));
        jsonArray.add(initItem("测试", "用来做一些技术测试", 1));


        jsonArray.add(initItem("", "功能", 0));
        jsonArray.add(initItem("qrcode", "qrcode识别", 1));
        jsonArray.add(initItem("camera", "相机", 1));
        jsonArray.add(initItem("statusBar", "侵入式体验", 1));
        jsonArray.add(initItem("authorizedOperation", "授权操作", 1));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerViewJsonArrayAdapter arrayAdapter = new RecyclerViewJsonArrayAdapter(jsonArray) {
            @Override
            protected int createViewHolder(int viewType) {
                int LAYOUT_ID = R.layout.item_recyclerview;
                switch (viewType) {
                    case 0:
                        LAYOUT_ID = R.layout.item_recyclerview_head;
                        break;
                    case 1:
                        LAYOUT_ID = R.layout.item_recyclerview;
                        break;
                }
                return LAYOUT_ID;
            }

            @Override
            protected void bindViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position) {
                switch (jsonObject.getInteger("viewType")) {
                    case 0:
                        viewHolder.setText(R.id.typeName, jsonObject.getString("name"));
                        break;
                    case 1:
                        viewHolder.setText(R.id.text, jsonObject.getString("name"));
                        break;
                }
            }
        };
        recyclerView.setAdapter(arrayAdapter);
        arrayAdapter.setOnClick(new RecyclerViewJsonArrayAdapter.Click() {
            @Override
            public void onClick(int position) {
                Log.d("点击", jsonArray.getJSONObject(position).getString("name"));
                menuClick(jsonArray.getJSONObject(position).getString("id"));
            }
        });

//        adapter.notifyDataSetChanged();
    }

    private void menuClick(String id) {
        switch (id) {
            case "rxjava":
                startActivity(new Intent(this, RXJavaActivity.class));
                break;
            case "测试":
                startActivity(new Intent(this, TestActivity.class));
                break;
            case "qrcode":
                startActivity(new Intent(this, QrCodeDemoActivity.class));
                break;
            case "camera":
                startActivity(new Intent(this, CameraActivity.class));
                break;
            case "statusBar":
                startActivity(new Intent(this, StatusBarActivity.class));
                break;
            case "authorizedOperation":
                startActivity(new Intent(this, AuthorizedOperationActivity.class));
                break;
        }
    }


    private JSONObject initItem(String id, String name, int type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("name", name);
        jsonObject.put("viewType", type);
        return jsonObject;
    }
}
