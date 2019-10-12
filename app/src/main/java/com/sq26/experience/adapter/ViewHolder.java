package com.sq26.experience.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

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

    //设置view背景(资源背景)
    public void setBackgroundResource(int viewId, int resId) {
        View v = view.findViewById(viewId);
        v.setBackgroundResource(resId);
    }

    //设置SimpleDraweeView的图片地址
    public void setImageURI(int viewId, String uriString) {
        SimpleDraweeView simpleDraweeView = view.findViewById(viewId);
        simpleDraweeView.setImageURI(uriString);
    }

    //设置view单击点击事件
    public void setOnClickListener(int viewId, View.OnClickListener l) {
        View v = view.findViewById(viewId);
        v.setOnClickListener(l);
    }

    //获取通过viewId控件
    public <T extends View> T getView(int viewId) {
        View v = view.findViewById(viewId);
        return (T) v;
    }
}
