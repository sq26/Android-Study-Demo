package com.sq26.experience.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sq26.experience.R;
import com.sq26.experience.adapter.CommonAdapter;
import com.sq26.experience.adapter.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.add)
    Button add;

    JSONArray jsonArray = new JSONArray();
    CommonAdapter commonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "1");
        jsonArray.add(jsonObject);
        commonAdapter = new CommonAdapter(R.layout.item_file, jsonArray) {
            @Override
            protected void bindViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position) {
                viewHolder.setText(R.id.name, jsonObject.getString("name"));
                switch (position) {
                    case 0:
                        viewHolder.itemView.setBackgroundResource(R.color.colorAccent);
                        break;
                    case 1:
                        viewHolder.itemView.setBackgroundResource(R.color.colorPrimary);
                        break;
                    case 2:
                        viewHolder.itemView.setBackgroundResource(R.color.colorPrimaryDark);
                        break;
                }
            }
        };

        recyclerView.setAdapter(commonAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//得到当前显示的最后一个item的view
                    View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount() - 1);
                    //得到lastChildView的top坐标值
                    int lastChildTop = lastChildView.getTop();
                    //得到recyclerBottom的底边坐标
                    int recyclerBottom = recyclerView.getBottom() - recyclerView.getPaddingBottom();
                    //recyclerView底座标减去最后一个view的顶部坐标大于最后一个view高度的一半,说明滑动超过了一半直接跳转到最后的item的位置
                    if (recyclerBottom - lastChildTop > lastChildView.getMeasuredHeight() / 2) {

                        int index = recyclerView.getLayoutManager().getPosition(lastChildView);
                        //设置recyclerVie的高度为即将要选中的view的高度
                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) recyclerView.getLayoutParams();
                        params.height = lastChildView.getMeasuredHeight();
                        recyclerView.setLayoutParams(params);
                        if (jsonArray.size() == 2) {
                            if (index == 0){
                                addTop();
                            }
                        } else {
                            if (index == 0){
                                addTopAndDelBottom();

                            }
                            if (index == jsonArray.size()-1){
                                addBottomAndDelTop();

                            }
                        }
                        recyclerView.smoothScrollToPosition(1);
                    } else
                        recyclerView.smoothScrollToPosition(recyclerView.getLayoutManager().getPosition(lastChildView) - 1);
                } else {
                }
            }
        });

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= 16) {
                    recyclerView.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(this);
                } else {
                    recyclerView.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                }
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) recyclerView.getLayoutParams();
                params.height = recyclerView.getLayoutManager().getChildAt(0).getMeasuredHeight();
                recyclerView.setLayoutParams(params);
                addTop();
            }
        });

    }

    private void addTop() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", jsonArray.size() + 1);
        jsonArray.add(0, jsonObject);
        commonAdapter.notifyItemInserted(0);
        commonAdapter.notifyItemRangeChanged(0, jsonArray.size()-1);
    }

    private void addBottom() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", jsonArray.size() + 1);
        jsonArray.add(jsonObject);
        commonAdapter.notifyItemInserted(jsonArray.size() - 1);
        commonAdapter.notifyItemRangeChanged(jsonArray.size() - 1, jsonArray.size());
    }

    private void delTop() {
        jsonArray.remove(0);
        commonAdapter.notifyItemInserted(0);
        commonAdapter.notifyItemRangeChanged(0, jsonArray.size());
    }

    private void delBottom() {
        jsonArray.remove(jsonArray.size() - 1);
        commonAdapter.notifyItemInserted(jsonArray.size() - 1);
        commonAdapter.notifyItemRangeChanged(jsonArray.size() - 1, jsonArray.size());
    }

    private void addBottomAndDelTop(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", jsonArray.size() + 1);
        jsonArray.add(jsonObject);
        commonAdapter.notifyItemInserted(jsonArray.size()-1);
        jsonArray.remove(0);
        commonAdapter.notifyItemRemoved(0);
        commonAdapter.notifyItemRangeChanged(0,jsonArray.size());
    }

    private void addTopAndDelBottom(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", jsonArray.size() + 1);
        jsonArray.add(0, jsonObject);
        commonAdapter.notifyItemInserted(0);
        jsonArray.remove(jsonArray.size() - 1);
        commonAdapter.notifyItemRemoved(jsonArray.size() - 1);
        commonAdapter.notifyItemRangeChanged(0,jsonArray.size());
    }

    private void SpeedComparison() {
        long time = 1489239412;
        long ling = 1000;

        long start = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < 2000; i++) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("time", time);
            jsonObject1.put("ling", ling);
            jsonObject.put("" + i, jsonObject1);
        }
        long end = System.currentTimeMillis();
        Log.d("JSONObject写速度", (end - start) + "");

        long start2 = System.currentTimeMillis();
        Map<String, Map> map = new HashMap<>();
        for (int i = 0; i < 2000; i++) {
            Map<String, Long> map2 = new HashMap<>();
            map2.put("time", time);
            map2.put("ling", ling);
            map.put("" + i, map2);
        }
        long end2 = System.currentTimeMillis();
        Log.d("HashMap写速度", (end2 - start2) + "");

        long start3 = System.currentTimeMillis();
        for (int i = 0; i < jsonObject.size(); i++) {
            jsonObject.getJSONObject("" + i).getInteger("");
        }
        long end3 = System.currentTimeMillis();
        Log.d("JSONObject读速度", (end3 - start3) + "");

        long start4 = System.currentTimeMillis();
        for (int i = 0; i < map.size(); i++) {
            map.get("" + i).get("ling");
        }
        long end4 = System.currentTimeMillis();
        Log.d("HashMap读速度", (end4 - start4) + "");
    }


}
