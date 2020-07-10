package com.sq26.experience.ui.activity.file;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.sq26.experience.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileImageActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_image);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        toolbar.setTitle(getString(R.string.imageBrowse));
        setSupportActionBar(toolbar);

    }
}