package com.sq26.experience.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sq26.experience.R;
import com.sq26.experience.adapter.CommonAdapter;
import com.sq26.experience.adapter.ItemTouchHelperCallback;
import com.sq26.experience.adapter.ViewHolder;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerViewActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

        toolbar.setTitle("RecyclerView");
        setSupportActionBar(toolbar);

        JSONArray jsonArray = new JSONArray();

        JSONObject jsonObject;
        for (int i = 0; i < 40; i++) {
            jsonObject = new JSONObject();
            jsonObject.put("text", i);
            jsonArray.add(jsonObject);
        }
        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //创建适配器
        CommonAdapter commonAdapter = new CommonAdapter(R.layout.item_recycler_view, jsonArray) {
            @Override
            protected void bindViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position) {
                viewHolder.setText(R.id.text, jsonObject.getString("text"));
                CardView cardView = viewHolder.getView(R.id.cardView);
                int r = random.nextInt(256);
                int g = random.nextInt(256);
                int b = random.nextInt(256);
                cardView.setCardBackgroundColor(Color.rgb(r, g, b));
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("点击的下标", "" + position);
                        Log.d("文本是", "" + jsonObject.getString("text"));
                        new AlertDialog.Builder(RecyclerViewActivity.this)
                                .setMessage("点击的下标" + position + "\n" + "文本是" + jsonObject.getString("text"))
                                .show();
                    }
                });
            }
        };
        //创建动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_recycler_item_show);
        //给适配器item设置显示动画
        commonAdapter.setItemAnimation(animation);
        //设置纵向分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //设置横向分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        //设置适配器
        recyclerView.setAdapter(commonAdapter);
        //创建item触摸气泡回调
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(commonAdapter);
        //创建iten触摸气泡
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        //设置recyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lookover, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_look_over:
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_view_list_white_24dp));
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                } else {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_view_module_white_24dp));
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
