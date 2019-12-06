package com.sq26.experience.adapter;

import com.alibaba.fastjson.JSONArray;

public abstract class CommonAdapter extends RecyclerViewAdapter {
    private int LAYOUT_ID;

    /**
     * 构造器
     *
     * @param jsonArray 数据集合
     */
    protected CommonAdapter(int layoutId, JSONArray jsonArray) {
        super(jsonArray);
        this.LAYOUT_ID = layoutId;
    }

    @Override
    protected int createViewHolder(int viewType) {
        return LAYOUT_ID;
    }
}
