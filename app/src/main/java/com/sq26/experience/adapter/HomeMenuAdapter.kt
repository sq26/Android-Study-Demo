package com.sq26.experience.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sq26.experience.data.HomeMenu
import com.sq26.experience.databinding.ItemRecyclerviewBinding
import com.sq26.experience.util.Log
import com.sq26.experience.viewmodel.MainViewModel

//获取数据模型
class HomeMenuAdapter(private val mainViewModel: MainViewModel) :
    ListAdapter<HomeMenu, HomeMenuAdapter.ViewHolder>(HomeMenuDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //数据绑定
        return ViewHolder(
            ItemRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //绑定数据
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            //设置数据的itemView的点击事件
            binding.setClickListener {
                //判断数据原是否存在
                binding.homeMenu?.apply {
                    //设置点击事件
                    Log.i("$this")
                    if (type == 0) {
                        //刷新菜单
                        mainViewModel.refreshHomeMenuList(id)
                    } else {
                        //设置要跳转的页面
                        mainViewModel.startTo(id)
                    }
                }
            }
        }

        fun bind(item: HomeMenu) {
            binding.apply {
                //设置数据源
                homeMenu = item
                //强制框架执行它到目前为止在绑定上需要做的所有事情.
                executePendingBindings()
            }
        }
    }

    class HomeMenuDiffCallback : DiffUtil.ItemCallback<HomeMenu>() {
        override fun areItemsTheSame(oldItem: HomeMenu, newItem: HomeMenu): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HomeMenu, newItem: HomeMenu): Boolean {
            return oldItem == newItem
        }
    }
}