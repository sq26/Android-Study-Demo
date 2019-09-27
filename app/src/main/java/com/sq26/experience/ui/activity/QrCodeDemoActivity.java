package com.sq26.experience.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sq26.experience.R;
import com.sq26.experience.util.permissions.JPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QrCodeDemoActivity extends AppCompatActivity {

    @BindView(R.id.codeText)
    TextView codeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_demo);
        ButterKnife.bind(this);
//        startActivity(new Intent(this,Capture));
    }

    @OnClick(R.id.scanIt)
    public void onViewClicked() {
        //初始化构造器
        JPermissions.init(this)
                //要申请的权限数组
                .permissions(new String[]{Manifest.permission.CAMERA})
                //成功的回调
                .success(new JPermissions.SuccessCallback() {
                    @Override
                    public void success() {
                        Log.e("success", "成功");
                    }
                })
                //不完全成功的回调
                .failure(new JPermissions.FailureCallback() {
                    @Override
                    public void failure(String[] successArray, String[] failureArray, String[] noPromptArray) {
                        Log.e("success", "失败");
                        //successArray成功的权限,failureArray,失败的权限,noPromptArray无法再次申请的权限
                    }
                })
                .start();

    }
}
