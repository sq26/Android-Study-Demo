package com.sq26.experience.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.sq26.experience.R;
import com.sq26.experience.util.DownloadManagement;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownloadManagementActivity extends AppCompatActivity {

    @BindView(R.id.uriText)
    EditText uriText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_management);
        ButterKnife.bind(this);
        uriText.setText("http://192.168.137.53:8080/123.exe");
    }

    @OnClick({R.id.button1, R.id.button2, R.id.button3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                //下载文件,检测要下载的url有没有下载过并检测下载过的文件是否还存在,没有下载过或已下载过不存在了再下载
                DownloadManagement.initialize(this, uriText.getText().toString())
                        .autoOpenFile(true)
                        .setOnComplete((path) -> {

                        })
                        .start();
                break;
            case R.id.button2:
                //下载并打开文件,在下载文件的校验基础加功能之后再执行打开文件操作
                break;
            case R.id.button3:
                //打开文件,检验文件路径是本地路径还是网络路径,网路路径走"下载并打开文件"的方法,本地路径,检擦文件是否存在,然后打开文件
                break;
        }
    }
}
