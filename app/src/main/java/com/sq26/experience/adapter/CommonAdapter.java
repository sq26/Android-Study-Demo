package com.sq26.experience.adapter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public abstract class CommonAdapter extends RecyclerViewJsonArrayAdapter {
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
