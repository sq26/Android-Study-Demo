package com.sq26.experience.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "HomeMenu")
data class HomeMenu(
    @PrimaryKey
    val id: String,
    val name: String
)

@Dao
interface HomeMenuDao {
    @Insert()
    fun insertAll(list: List<HomeMenu>)

    @Query("select * from HomeMenu")
    fun getHomeMenuList(): Flow<List<HomeMenu>>
}