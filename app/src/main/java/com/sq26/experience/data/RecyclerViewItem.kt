package com.sq26.experience.data

import android.graphics.Color
import androidx.recyclerview.widget.DiffUtil
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Entity
data class RecyclerViewItem(
    @PrimaryKey(autoGenerate = true)
    //颜色
    val id: Int = 0,
    var sort: Int = 0,
    val color: Int = getColor()
)

private fun getColor(): Int {
    val random = Random()
    val r = random.nextInt(256)
    val g = random.nextInt(256)
    val b = random.nextInt(256)
    return Color.rgb(r, g, b)
}

//DiffUtil是一个实用程序类，它计算两个列表之间的差异并输出
//将一个列表转换为另一个列表的更新操作列表。
class RecyclerViewItemCallback : DiffUtil.ItemCallback<RecyclerViewItem>() {
    override fun areItemsTheSame(
        oldItem: RecyclerViewItem,
        newItem: RecyclerViewItem
    ): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: RecyclerViewItem,
        newItem: RecyclerViewItem
    ): Boolean {
        return oldItem == newItem
    }
}