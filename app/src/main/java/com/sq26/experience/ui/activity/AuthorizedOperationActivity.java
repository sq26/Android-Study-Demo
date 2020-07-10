package com.sq26.experience.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sq26.experience.R;
import com.sq26.experience.adapter.CommonAdapter;
import com.sq26.experience.adapter.ViewHolder;
import com.sq26.experience.util.permissions.JPermissions;
import com.sq26.experience.util.permissions.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthorizedOperationActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.button)
    Button button;

    private JSONArray jsonArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorized_operation);
        ButterKnife.bind(this);


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "日历数据");
        jsonObject.put("select", false);
        jsonObject.put("content", PermissionUtil.READ_CALENDAR);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("name", "相机");
        jsonObject.put("select", false);
        jsonObject.put("content", PermissionUtil.CAMERA);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("name", "联系人");
        jsonObject.put("select", false);
        jsonObject.put("content", PermissionUtil.READ_CONTACTS);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("name", "位置");
        jsonObject.put("select", false);
        jsonObject.put("content", PermissionUtil.ACCESS_FINE_LOCATION);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("name", "麦克风");
        jsonObject.put("select", false);
        jsonObject.put("content", PermissionUtil.RECORD_AUDIO);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("name", "电话");
        jsonObject.put("select", false);
        jsonObject.put("content", PermissionUtil.READ_PHONE_STATE);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("name", "传感器");
        jsonObject.put("select", false);
        jsonObject.put("content", PermissionUtil.BODY_SENSORS);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("name", "短信");
        jsonObject.put("select", false);
        jsonObject.put("content", PermissionUtil.SEND_SMS);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("name", "存储");
        jsonObject.put("select", false);
        jsonObject.put("content", PermissionUtil.READ_EXTERNAL_STORAGE);
        jsonArray.add(jsonObject);
        CommonAdapter commonAdapter = new CommonAdapter(R.layout.item_authorized_operation, jsonArray) {
            @Override
            public void bindViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position, Object payload) {
                viewHolder.setText(R.id.text, jsonObject.getString("name"));
                CheckBox checkBox = viewHolder.getView(R.id.checkbox);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        jsonObject.put("select", b);
                    }
                });
            }
        };
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(commonAdapter);

    }


    private void startPermissions(String[] permissions) {
        //初始化构造器
        JPermissions.init(this)
                //要申请的权限数组
                .permissions(permissions)
                //成功的回调
                .success(new JPermissions.SuccessCallback() {
                    @Override
                    public void success() {
                        Log.e("success", "成功");
                        Toast.makeText(AuthorizedOperationActivity.this, "申请成功", Toast.LENGTH_LONG).show();
                    }
                })
                //不完全成功的回调
                .failure(new JPermissions.FailureCallback() {
                    @Override
                    public void failure(String[] successArray, String[] failureArray, String[] noPromptArray) {
                        //successArray成功的权限,failureArray,失败的权限,noPromptArray无法再次申请的权限
                        Log.e("success", "失败");
                        Toast.makeText(AuthorizedOperationActivity.this, "申请失败", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .start();
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        List<String> permissionList = new ArrayList<>();//可以去申请的权限列表
        for (int i = 0; i < jsonArray.size(); i++) {
            if (jsonArray.getJSONObject(i).getBoolean("select"))
                permissionList.add(jsonArray.getJSONObject(i).getString("content"));
        }
        startPermissions(permissionList.toArray(new String[0]));
    }
}
