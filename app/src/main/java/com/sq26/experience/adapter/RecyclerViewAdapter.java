package com.sq26.experience.adapter;

import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    /**
     * 定义抽象方法,用于移动个itemView位置的实现
     * @param fromPosition 移动前的位置
     * @param toPosition    移动后的位置
     */
    public abstract void onItemViewMove(int fromPosition, int toPosition) ;

    /**
     * 定义抽象方法,用于移除某个itemView的实现
     * @param position 要移除itemView的下标
     */
    public abstract void onClearItemView(int position) ;

}
