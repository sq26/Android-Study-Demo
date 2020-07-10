package com.sq26.experience.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Collections;
import java.util.List;

public abstract class RecyclerViewJSONArrayAdapter extends RecyclerViewAdapter {
    private JSONArray array;//数据集合

    /**
     * 构造器
     *
     * @param jsonArray 数据集合
     */
    public RecyclerViewJSONArrayAdapter(JSONArray jsonArray) {
        this.array = jsonArray;
    }

    /**
     * 定义抽象方法,用于在使用层定义view内容
     *
     * @param viewType 视图类型
     */
    public abstract int createViewHolder(int viewType);

    /**
     * 定义抽象方法,用于在使用层定义view内容
     *
     * @param viewHolder 根view
     * @param jsonObject 对应下标的单条jsonObject(在没有换位或多线程对数据操作下
     *                   一般不会有问题,如有问题使用传入的数据源通过position获取jsonObject)
     * @param position   数据下标
     * @param payload   刷新标识
     */
    public abstract void bindViewHolder(ViewHolder viewHolder, JSONObject jsonObject, int position, Object payload);

    //在这里创建生成页面布局
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //使用抽象方法从使用层获取布局(设置布局放在使用层,可以动态配置多种item)
        int LAYOUT_ID = createViewHolder(viewType);
        //LayoutInflater.from指定写法
        View view = LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false);
        return new ViewHolder(view);
    }

    //在这里设置业务数据和各种事件
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //利用抽象方法,让使用层去实现设置业务数据
//        bindViewHolder(holder, array.getJSONObject(position), position);
        //这个 不用了,这个方法不做任何处理
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            bindViewHolder(holder, array.getJSONObject(position), position, null);
        } else {
            bindViewHolder(holder, array.getJSONObject(position), position, payloads.get(0));
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

    /**
     * 设置交换item位置的实现
     *
     * @param fromPosition 移动前的位置
     * @param toPosition   移动后的位置
     */
    @Override
    public void onItemViewMove(int fromPosition, int toPosition) {
        //交换在指定列表中的指定位置的元素。
        Collections.swap(array, fromPosition, toPosition);
        //从 fromPosition 移动到 toPosition 为止时刷新。
        notifyItemMoved(fromPosition, toPosition);
        Log.i("toPosition", toPosition + "");
        //刷新这个区间的数据,以防止出错
        //开始位置取最小值,刷新数量取差值加1
        notifyItemRangeChanged(Math.min(fromPosition, toPosition),
                (Math.max(fromPosition, toPosition) - Math.min(fromPosition, toPosition) + 1),true);
    }

    //移除itemView的实现
    @Override
    public void onClearItemView(int position) {
        //从数据集中移除指定下标的数据
        array.remove(position);
        //从视图中移除指定下标的数据
        notifyItemRemoved(position);
        //刷新从指定的下标到数据集总量的页面数据,并设定不刷新视图只刷新数据
        notifyItemRangeChanged(position, array.size() - position, true);
    }
}
