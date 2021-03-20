package com.sq26.experience.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * 通用ListAdapter
 * 构造函数layoutId或createView函数必须实现一个
 */

abstract class CommonListAdapter<T>(
    diffCallback: DiffUtil.ItemCallback<T>,
) : ListAdapter<T, CommonListViewHolder<*>>(diffCallback) {
    //可选实现函数,用于多类型视图匹配,如果不填layoutId或不重写此函数就必然会异常
    abstract fun createView(parent: ViewGroup, viewType: Int): CommonListViewHolder<*>

    //创建ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonListViewHolder<*> {
        //创建CommonListViewHolder
        return createView(parent, viewType)
    }

    override fun onBindViewHolder(holder: CommonListViewHolder<*>, position: Int) {
        //绑定视图,由调用页面绑定
        holder.bind(position)
    }
}

//通用ListAdapter的ViewModel
abstract class CommonListViewHolder<V : ViewDataBinding>(val v: V) :
    RecyclerView.ViewHolder(v.root) {
    //绑定数据操作
    abstract fun bind(position: Int)
}