package com.sq26.experience.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sq26.experience.R
import com.sq26.experience.data.RecyclerViewItem
import com.sq26.experience.databinding.ItemRecyclerviewItemBinding
import com.sq26.experience.util.Log

class RecyclerView1Adapter :
    ListAdapter<RecyclerViewItem, RecyclerView1Adapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemRecyclerviewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recycler_item_show)
        holder.itemView.startAnimation(animation)
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val itemRecyclerviewItemBinding: ItemRecyclerviewItemBinding) :
        RecyclerView.ViewHolder(itemRecyclerviewItemBinding.root) {

        init {
            itemRecyclerviewItemBinding.root.setOnClickListener {
                itemRecyclerviewItemBinding.item?.apply {
                    Log.i(this.id.toString(), "Recyclerview")
                }

            }
        }

        fun getItem() = itemRecyclerviewItemBinding.item

        fun bind(recyclerViewItem: RecyclerViewItem) {
            itemRecyclerviewItemBinding.apply {
                item = recyclerViewItem
            }
        }
    }

    //DiffUtil是一个实用程序类，它计算两个列表之间的差异并输出
    //将一个列表转换为另一个列表的更新操作列表。
    class DiffCallback : DiffUtil.ItemCallback<RecyclerViewItem>() {
        //判断唯一标识是否相同
        override fun areItemsTheSame(
            oldItem: RecyclerViewItem,
            newItem: RecyclerViewItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        //判断内容是否相同
        override fun areContentsTheSame(
            oldItem: RecyclerViewItem,
            newItem: RecyclerViewItem
        ): Boolean {
            return oldItem == newItem
        }

    }
}