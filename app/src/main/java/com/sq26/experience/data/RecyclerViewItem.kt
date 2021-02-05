package com.sq26.experience.data

import android.graphics.Color
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

@Dao
interface RecyclerViewDao {
    @Insert
    fun insert(recyclerViewItem: RecyclerViewItem)

    @Update
    fun updateAll(recyclerViewList: List<RecyclerViewItem>)

    @Delete
    fun delete(recyclerViewItem: RecyclerViewItem)

    @Insert
    fun insertAll(recyclerViewList: List<RecyclerViewItem>)

    @Query("select * from RecyclerViewItem order by sort asc")
    fun queryAll(): Flow<List<RecyclerViewItem>>

    @Query("select count(1) from RecyclerViewItem")
    fun queryCount(): Int

    @Query("select * from RecyclerViewItem limit :start, :limit")
    fun queryPagingAll(start: Int, limit: Int): List<RecyclerViewItem>

    //获取sort的当前最大值
    @Query("select max(sort) from RecyclerViewItem")
    fun getMaxSort(): Int

    //删除所有数据
    @Query("delete from RecyclerViewItem")
    fun deleteAll()
}