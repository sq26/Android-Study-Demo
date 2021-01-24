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

    @Insert
    fun insertAll(recyclerViewList: List<RecyclerViewItem>)

    @Query("select * from RecyclerViewItem")
    fun queryAll(): Flow<List<RecyclerViewItem>>
}