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
import com.sq26.experience.adapter.ViewHolder;

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
        //菜单列表
        JSONArray jsonArray = new JSONArray();
        //id是点击事件的id,(使用string类型是为了方便阅读代码)
        //name是显示在界面上的内容
        //type是显示的布局类型,0是类型,1是条目
        jsonArray.add(initItem("", getString(R.string.Java_technology), 0));
        jsonArray.add(initItem("RxJava", getString(R.string.Use_of_RxJava), 1));
        jsonArray.add(initItem("encryption", getString(R.string.Symmetric_and_asymmetric_encryption), 1));
        jsonArray.add(initItem("aidl", getString(R.string.AIDL_inter_process_communication), 1));
        jsonArray.add(initItem("测试", "用来做一些技术测试", 1));

        jsonArray.add(initItem("", getString(R.string.Features), 0));
        jsonArray.add(initItem("QRCode", getString(R.string.QRCode_recognition), 1));
        jsonArray.add(initItem("camera", getString(R.string.camera), 1));
        jsonArray.add(initItem("statusBar", getString(R.string.Invasive_experience), 1));
        jsonArray.add(initItem("authorizedOperation", getString(R.string.Authorized_operation), 1));
        jsonArray.add(initItem("fileManagement", getString(R.string.file_management), 1));
        jsonArray.add(initItem("mediaManagement", getString(R.string.media_management), 1));
        jsonArray.add(initItem("downloadManagement", getString(R.string.download_management), 1));
        jsonArray.add(initItem("databaseManagement", getString(R.string.Database_operation), 1));
        jsonArray.add(initItem("network", getString(R.string.network), 1));

        jsonArray.add(initItem("", getString(R.string.view), 0));
        jsonArray.add(initItem("pullToRefresh", getString(R.string.PullDownToRefresh), 1));
        jsonArray.add(initItem("RecyclerView", getString(R.string.RecyclerView_use), 1));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerViewAdapter arrayAdapter = new RecyclerViewAdapter(jsonArray) {
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
        arrayAdapter.setOnClick(new RecyclerViewAdapter.OnClick() {
            @Override
            public void click(JSONObject jsonObject, int position) {
                Log.d("点击", jsonObject.getString("name"));
                menuClick(jsonObject.getString("id"));
            }
        });

//        adapter.notifyDataSetChanged();
    }

    private void menuClick(String id) {
        switch (id) {
            case "RxJava":
                startActivity(new Intent(this, RXJavaActivity.class));
                break;
            case "测试":
                startActivity(new Intent(this, TestActivity.class));
                break;
            case "QRCode":
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
            case "fileManagement":
                startActivity(new Intent(this, FileManagementActivity.class));
                break;
            case "mediaManagement":
                startActivity(new Intent(this, MediaManagementActivity.class));
                break;
            case "downloadManagement":
                startActivity(new Intent(this, DownloadManagementActivity.class));
                break;
            case "pullToRefresh":
                startActivity(new Intent(this, PullToRefreshActivity.class));
                break;
            case "RecyclerView":
                startActivity(new Intent(this, RecyclerViewActivity.class));
                break;
            case "encryption":
                startActivity(new Intent(this, EncryptionActivity.class));
                break;
            case "network":
                startActivity(new Intent(this, NetworkActivity.class));
                break;
            case "aidl":
                startActivity(new Intent(this, NetworkActivity.class));
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
