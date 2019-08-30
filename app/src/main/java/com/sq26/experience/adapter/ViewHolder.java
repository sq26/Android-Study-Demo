package com.sq26.experience.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    private View view;

    ViewHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
    }

    //设置text文本
    public void setText(int viewId, String value) {
        TextView textView = view.findViewById(viewId);
        textView.setText(value);
    }

    //获取通过viewId控件
    public <T extends View> T getView(int viewId) {
        View v = view.findViewById(viewId);
        return (T) v;
    }
}
