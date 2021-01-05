package com.sq26.experience.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "HomeMenu")
data class HomeMenu(
    @PrimaryKey
    val id: String,
    val name: String,
    //HomeMenuType的id
    val type: Int
)

@Dao
interface HomeMenuDao {
    //插入一组HomeMenu
    @Insert()
    fun insertAll(list: List<HomeMenu>)
    //获取HomeMenu表所有数据并监听HomeMenu表
    @Query("select * from HomeMenu")
    fun getHomeMenuList(): Flow<List<HomeMenu>>
    //清空HomeMenu表
    @Query("delete from HomeMenu")
    fun deleteAll()
}