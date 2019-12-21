package com.sq26.experience.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sq26.experience.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AIDLActivity extends AppCompatActivity {

    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.sq26.experience.service.AIDLService","com.sq26.experience");
    }
}
