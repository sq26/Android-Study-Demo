package com.sq26.experience.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public abstract class RecyclerViewJsonArrayAdapter extends RecyclerView.Adapter<ViewHolder> {
    private JSONArray array;//数据集合
    private Click click;//点击事件

    /**
     * 构造器
     *
     * @param jsonArray 数据集合
     */
    protected RecyclerViewJsonArrayAdapter(JSONArray jsonArray) {
        this.array = jsonArray;
    }

    /**
     * 定义抽象方法,用于在使用层定义view内容
     *
     * @param viewType 视图类型
     */
    protected abstract int createViewHolder(int viewType);

    /**
     * 定义抽象方法,用于在使用层定义view内容
     *
     * @param viewHolder 根view
     * @param jsonObject 对应下标的单条jsonObject
     * @param position   数据下标
     */
    protected abstract void bindViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position);

    //在这里创建生成页面布局
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //使用抽象方法从使用层获取布局(设置布局放在使用层,可以动态配置多种item)
        int LAYOUT_ID = createViewHolder(viewType);
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false);
        return new ViewHolder(v);
    }

    //在这里设置业务数据和各种事件
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //利用抽象方法,让使用层去实现设置业务数据
        bindViewHolder(holder, array.getJSONObject(position), position);
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

    @Override
    public int getItemViewType(int position) {
        if (array.getJSONObject(position) != null) {
            if (array.getJSONObject(position).getInteger("viewType") != null) {
                return array.getJSONObject(position).getInteger("viewType");
            } else {
                return super.getItemViewType(position);
            }
        } else {
            return super.getItemViewType(position);
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
