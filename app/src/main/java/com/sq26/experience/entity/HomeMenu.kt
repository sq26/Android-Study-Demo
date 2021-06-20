package com.sq26.experience.entity

import androidx.recyclerview.widget.DiffUtil

data class HomeMenu(
    //id唯一标识
    val id: String,
    //显示名称
    val name: String,
    //标识是菜单类型还是菜单
    val type: Int,
    //给菜单用,记录菜单类型的id
    val typeId: String = ""
)

class HomeMenuDiffCallback : DiffUtil.ItemCallback<HomeMenu>() {
    override fun areItemsTheSame(oldItem: HomeMenu, newItem: HomeMenu): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HomeMenu, newItem: HomeMenu): Boolean {
        return oldItem == newItem
    }
}
