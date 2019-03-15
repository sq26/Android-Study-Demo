package com.sq26.androidstudydemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.sq26.androidstudydemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StatusBarActivity extends AppCompatActivity {

    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.checkbox1)
    CheckBox checkbox1;
    @BindView(R.id.checkbox2)
    CheckBox checkbox2;
    @BindView(R.id.checkbox3)
    CheckBox checkbox3;
    @BindView(R.id.checkbox4)
    CheckBox checkbox4;
    @BindView(R.id.checkbox5)
    CheckBox checkbox5;
    @BindView(R.id.checkbox6)
    CheckBox checkbox6;
    @BindView(R.id.checkbox7)
    CheckBox checkbox7;
    @BindView(R.id.checkbox8)
    CheckBox checkbox8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_bar);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button1)
    public void onViewClicked() {
        View decorView = getWindow().getDecorView();
        Integer uiOptions = 0;
        if (checkbox1.isChecked()){
            uiOptions = uiOptions | View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        if (checkbox2.isChecked()){
            uiOptions = uiOptions | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        if (checkbox3.isChecked()){
            uiOptions = uiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        if (checkbox4.isChecked()){
            uiOptions = uiOptions | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }
        if (checkbox5.isChecked()){
            uiOptions = uiOptions | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        }
        if (checkbox6.isChecked()){
            uiOptions = uiOptions | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        }
        if (checkbox7.isChecked()){
            uiOptions = uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        if (checkbox8.isChecked()){
            uiOptions = uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        decorView.setSystemUiVisibility(uiOptions);
    }
}
