package com.sq26.experience.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sq26.experience.R;
import com.sq26.experience.adapter.RecyclerViewJSONArrayAdapter;
import com.sq26.experience.adapter.ViewHolder;
import com.sq26.experience.entity.JsonArrayViewMode;
import com.sq26.experience.ui.activity.file.FileHomeActivity;

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
//        initView();
        init();
    }

    private void initView(){
        JsonArrayViewMode jsonArrayViewMode = new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(JsonArrayViewMode.class);

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
        jsonArray.add(initItem("kotlin学习", "用来做kotlin语言学习", 1));

        jsonArray.add(initItem("", getString(R.string.AndroidJetpackComponents), 0));
        jsonArray.add(initItem("Lifecycle", getString(R.string.LifecycleComponents), 1));
        jsonArray.add(initItem("LiveData", getString(R.string.LiveDataComponents), 1));
        jsonArray.add(initItem("ViewMode", getString(R.string.ViewModeComponents), 1));
        jsonArray.add(initItem("Navigation", getString(R.string.NavigationComponents), 1));
        jsonArray.add(initItem("Room", getString(R.string.RoomComponents), 1));
        jsonArray.add(initItem("Paging", getString(R.string.PagingComponents), 1));
        jsonArray.add(initItem("WorkManger", getString(R.string.WorkMangerComponents), 1));

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
        jsonArray.add(initItem("WiFiDirect", getString(R.string.WiFi_Direct), 1));
        jsonArray.add(initItem("AppManagement", getString(R.string.app_management), 1));

        jsonArray.add(initItem("", getString(R.string.view), 0));
        jsonArray.add(initItem("pullToRefresh", getString(R.string.PullDownToRefresh), 1));
        jsonArray.add(initItem("RecyclerView", getString(R.string.RecyclerView_use), 1));

        RecyclerViewJSONArrayAdapter arrayAdapter = new RecyclerViewJSONArrayAdapter(jsonArray) {
            @Override
            public int createViewHolder(int viewType) {
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
            public void bindViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position, Object payload) {
                switch (jsonObject.getInteger("viewType")) {
                    case 0:
                        viewHolder.setText(R.id.typeName, jsonObject.getString("name"));
                        break;
                    case 1:
                        viewHolder.setText(R.id.text, jsonObject.getString("name"));
                        break;
                }
                viewHolder.itemView.setOnClickListener(view -> {
                    Log.d("点击", jsonObject.getString("name"));
                    menuClick(jsonObject.getString("id"));
                });
            }
        };
        recyclerView.setAdapter(arrayAdapter);

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
                startActivity(new Intent(this, FileHomeActivity.class));
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
                startActivity(new Intent(this, AIDLActivity.class));
                break;
            case "Lifecycle":
                startActivity(new Intent(this, LifecycleActivity.class));
                break;
            case "LiveData":
                startActivity(new Intent(this, LiveDataActivity.class));
                break;
            case "ViewMode":
                startActivity(new Intent(this, ViewModeActivity.class));
                break;
            case "Navigation":
                startActivity(new Intent(this, NavigationActivity.class));
                break;
            case "Room":
                startActivity(new Intent(this, RoomActivity.class));
                break;
            case "Paging":
                startActivity(new Intent(this, PagingActivity.class));
                break;
            case "WiFiDirect":
                startActivity(new Intent(this, WiFiDirectActivity.class));
                break;
            case "AppManagement":
                startActivity(new Intent(this, AppManagementActivity.class));
                break;
            case "kotlin学习":
                startActivity(new Intent(this, KotlinActivity.class));
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
