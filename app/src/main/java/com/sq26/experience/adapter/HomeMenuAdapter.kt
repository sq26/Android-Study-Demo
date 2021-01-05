package com.sq26.experience.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sq26.experience.data.HomeMenu
import com.sq26.experience.databinding.ItemRecyclerviewBinding
import com.sq26.experience.ui.fragment.HomeFragmentDirections
import com.sq26.experience.util.Log


class HomeMenuAdapter : ListAdapter<HomeMenu, HomeMenuAdapter.ViewHolder>(HomeMenuDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            //设置数据的itemView的点击事件
            binding.setClickListener {
                //判断数据原是否存在
                binding.homeMenu?.apply {
                    //设置点击事件
                    Log.i("$this")
                    //设置要跳转的页面
                    val directions = when(id){
                        "encryption"->
                            HomeFragmentDirections.actionHomeFragmentToEncryptionActivity()
                        "Navigation"->
                            HomeFragmentDirections.actionHomeFragmentToNavigationActivity()
                        else ->
                            HomeFragmentDirections.actionHomeFragmentToStartFragment()
                    }

                    it.findNavController().navigate(directions)
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
}

private class HomeMenuDiffCallback : DiffUtil.ItemCallback<HomeMenu>() {
    override fun areItemsTheSame(oldItem: HomeMenu, newItem: HomeMenu): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HomeMenu, newItem: HomeMenu): Boolean {
        return oldItem == newItem
    }
}