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

    public void setText(int viewId, String value) {
        TextView textView = view.findViewById(viewId);
        textView.setText(value);
    }

}
