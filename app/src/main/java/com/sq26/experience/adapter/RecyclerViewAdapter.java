package com.sq26.experience.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Collections;

public abstract class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {
    private JSONArray array;//数据集合
    private OnClick onClick;//点击事件
    private Animation animation;//item显示动画

    /**
     * 构造器
     *
     * @param jsonArray 数据集合
     */
    protected RecyclerViewAdapter(JSONArray jsonArray) {
        this.array = jsonArray;
    }

    //设置item的显示动画
    public void setItemAnimation(Animation animation) {
        this.animation = animation;
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
        //判断显示动画有没有设置
        if (animation != null)
            //设置显示动画
            holder.itemView.startAnimation(animation);

        //利用抽象方法,让使用层去实现设置业务数据
        bindViewHolder(holder, array.getJSONObject(position), position);

        //判断有没有设置点击事件
        if (onClick != null) {
            //有点击事件就去注册点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //这里的判断是为了预防一个特殊的bug,当正在刷新列表的时候点击了item中的某个条目,
                    // 这时列表数据改变了,界面还没有刷新,会导致数组下标越界,这个判断只能预防这个错误,并不能有效解决,
                    //还是有可能进入错误的item
                    if (position < array.size())
                        //获得点击事件后,回调给使用层
                        onClick.click(array.getJSONObject(position), position);
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        //判断item是不是null
        if (array.getJSONObject(position) != null) {
            //判断有没有设置type类型
            if (array.getJSONObject(position).getInteger("viewType") != null) {
                //有设置返回指定的视图类型
                return array.getJSONObject(position).getInteger("viewType");
            } else {
                //没有设置返回默认值1
                return 1;
            }
        } else {
            //空的设置返回默认值0
            return 1;
        }
    }

    //设置iten的数据长度
    @Override
    public int getItemCount() {
        return array.size();
    }

    //设置点击事件的方法
    public void setOnClick(OnClick callback) {
        this.onClick = callback;
    }

    //设置点击事件的方法
    public void onItemMove(int fromPosition, int toPosition) {
        //交换在指定列表中的指定位置的元素。
        Collections.swap(array, fromPosition, toPosition);
        //从 fromPosition 移动到 toPosition 为止时刷新。
        notifyItemMoved(fromPosition, toPosition);
    }

    //点击事件的接口,由使用层去实现
    public interface OnClick {
        abstract void click(JSONObject jsonObject, int position);
    }
}
