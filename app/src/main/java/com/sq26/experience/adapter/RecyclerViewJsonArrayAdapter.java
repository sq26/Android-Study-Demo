package com.sq26.experience.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public abstract class RecyclerViewJsonArrayAdapter extends RecyclerView.Adapter<ViewHolder> {
    private int LAYOUT_ID;//要使用的layout布局
    private JSONArray array;//数据集合
    private Click click;//点击事件

    /**
     * 构造器
     *
     * @param layoutId  ui布局的id
     * @param jsonArray 数据集合
     */
    protected RecyclerViewJsonArrayAdapter(int layoutId, JSONArray jsonArray) {
        this.array = jsonArray;
        this.LAYOUT_ID = layoutId;
    }

    /**
     * 定义抽象方法,用于在使用层定义view内容
     *
     * @param viewHolder 根view
     * @param jsonObject 对应下标的单条jsonObject
     * @param position   数据下标
     */
    protected abstract void createViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position);

    //在这里创建生成页面布局
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false);
        return new ViewHolder(v);
    }

    //在这里设置业务数据和各种事件
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //利用抽象方法,让使用层去实现设置业务数据
        createViewHolder(holder, array.getJSONObject(position), position);
        //判断有没有设置点击事件
        if (click != null) {
            //有点击事件就去注册点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //获得点击事件后,回调给使用层
                    click.onClick(position);
                }
            });
        }

    }

    //设置iten的数据长度
    @Override
    public int getItemCount() {
        return array.size();
    }

    //设置点击事件的方法
    public void setOnClick(Click callback) {
        this.click = callback;
    }

    //点击事件的接口,由使用层去实现
    public interface Click {
        abstract void onClick(int position);
    }
}
