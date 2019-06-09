package com.sq26.experience.ui.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sq26.experience.R;
import com.sq26.experience.adapter.RecyclerViewAdapter;
import com.sq26.experience.adapter.RecyclerViewJsonArrayAdapter;
import com.sq26.experience.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        init();
    }

    private void init() {
        List<JSONObject> list = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        for (int i = 1; i < 10; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text", i);
            jsonArray.add(jsonObject);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        RecyclerViewAdapter<JSONObject> adapter = new RecyclerViewAdapter<JSONObject>(R.layout.item_recyclerview, list) {
//
//            @Override
//            protected void createViewHolder(ViewHolder viewHolder, JSONObject object, int position) {
//                viewHolder.setText(R.id.text, object.getString("text"));
//            }
//        };

        RecyclerViewJsonArrayAdapter arrayAdapter = new RecyclerViewJsonArrayAdapter(R.layout.item_recyclerview, jsonArray) {
            @Override
            protected void createViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position) {
                viewHolder.setText(R.id.text, jsonObject.getString("text"));
            }
        };
        recyclerView.setAdapter(arrayAdapter);
        arrayAdapter.setOnClick(new RecyclerViewJsonArrayAdapter.Click() {
            @Override
            public void onClick(int position) {
                Log.d("点击",jsonArray.getJSONObject(position).getString("text"));
            }
        });

//        adapter.notifyDataSetChanged();
    }
}
