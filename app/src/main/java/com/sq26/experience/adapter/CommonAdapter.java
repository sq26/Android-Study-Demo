package com.sq26.experience.adapter;

import com.alibaba.fastjson.JSONArray;

public abstract class CommonAdapter extends RecyclerViewJSONArrayAdapter {
    private int LAYOUT_ID;

    /**
     * 构造器
     *
     * @param jsonArray 数据集合
     */
    public CommonAdapter(int layoutId, JSONArray jsonArray) {
        super(jsonArray);
        this.LAYOUT_ID = layoutId;
    }

    @Override
    public int createViewHolder(int viewType) {
        return LAYOUT_ID;
    }
}
