package com.sq26.experience.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class RecyclerViewListAdapter<T> extends RecyclerViewAdapter {
    private int LAYOUT_ID;
    private List<T> array;

    protected RecyclerViewListAdapter(int layoutId, List<T> list) {
        this.LAYOUT_ID = layoutId;
        this.array = list;
    }

    protected abstract void bindViewHolder(ViewHolder viewHolder, T item, int position);

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        bindViewHolder(holder, array.get(position), position);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    @Override
    public void onItemViewMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onClearItemView(int position) {

    }
}
