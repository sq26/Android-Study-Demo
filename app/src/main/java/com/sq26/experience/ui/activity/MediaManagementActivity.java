package com.sq26.experience.ui.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sq26.experience.R;
import com.sq26.experience.adapter.CommonAdapter;
import com.sq26.experience.adapter.RecyclerViewAdapter;
import com.sq26.experience.adapter.ViewHolder;
import com.sq26.experience.ui.dialog.ProgressDialog;
import com.sq26.experience.util.DensityUtil;
import com.sq26.experience.util.FileUtil;
import com.sq26.experience.util.ImageCompressionUtil;
import com.sq26.experience.util.media.JImage;
import com.sq26.experience.util.media.SimpleDraweeViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MediaManagementActivity extends AppCompatActivity {

    @BindView(R.id.preview)
    SimpleDraweeView preview;
    @BindView(R.id.imageRecyclerView)
    RecyclerView imageRecyclerView;

    private CommonAdapter imageAdapter;
    private JSONArray imageArray = new JSONArray();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_management);
        ButterKnife.bind(this);
        context = this;
        init();
    }

    private void init() {
        imageAdapter = new CommonAdapter(R.layout.item_recyclerview, imageArray) {
            @Override
            protected void bindViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position) {
                viewHolder.setText(R.id.text, jsonObject.getString("name"));
            }
        };
        imageRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        imageRecyclerView.setAdapter(imageAdapter);
        imageAdapter.setOnClick(new RecyclerViewAdapter.OnClick() {
            @Override
            public void click(JSONObject jsonObject, int position) {
                SimpleDraweeViewUtils.setDraweeController(jsonObject.getString("path"), preview, DensityUtil.dip2px(context, 150));
            }
        });
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
                                JSONObject item;
                                for (String p : path) {
                                    Log.d("getImage", p);
                                    item = new JSONObject();
                                    item.put("name", FileUtil.getFileName(p));
                                    if (FileUtil.isAbsolutePath(p)) {
                                        item.put("path", p);
                                    } else {
                                        item.put("path", ImageCompressionUtil.startCompression(context, Uri.parse(p)));
                                    }
                                    imageArray.add(item);
                                }
                                imageAdapter.notifyDataSetChanged();
                            }
                        }).start();

                break;
            case R.id.getVideo:
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("123");
                progressDialog.show();

                ProgressDialog progressDialog2 = new ProgressDialog(this);
                progressDialog2.setMessage("321");
                progressDialog2.show();
                break;
        }
    }
}
