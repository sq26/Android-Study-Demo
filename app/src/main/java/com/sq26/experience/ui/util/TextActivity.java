package com.sq26.experience.ui.util;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sq26.experience.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextActivity extends AppCompatActivity {

    @BindView(R.id.textView)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        ButterKnife.bind(this);
        //获取到系统intent
        Intent intent = getIntent();
        //判断是不是从其它应用过来的
        if (Objects.equals(intent.getAction(), Intent.ACTION_VIEW)) {
            try {
                //根据uri获取InputStream输入流
                InputStream inputStream = getContentResolver().openInputStream(intent.getData());
                //创建byte数组
                byte[] bytes = new byte[inputStream.available()];
                //一次性读取全部文本
                inputStream.read(bytes);
                //转成string类型
                String string = new String(bytes);
                //设置到textView上做显示
                textView.setText(string);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
