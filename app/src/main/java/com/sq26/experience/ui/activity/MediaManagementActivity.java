package com.sq26.experience.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.sq26.experience.R;
import com.sq26.experience.util.media.JImage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MediaManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_management);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.getImage, R.id.getVideo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.getImage:
                JImage.initialize(this)
                        .setImageSource(JImage.ALL)
                        .success(new JImage.SuccessCallback() {
                            @Override
                            public void success(String... path) {

                            }
                        }).start();

                break;
            case R.id.getVideo:
                break;
        }
    }
}
