package com.sq26.experience.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "HomeMenu")
data class HomeMenu(
    //id唯一标识
    @PrimaryKey
    val id: String,
    //显示名称
    val name: String,
    //标识是菜单类型还是菜单
    val type: Int,
    //给菜单用,记录菜单类型的id
    val typeId: String = ""
)

@Dao
interface HomeMenuDao {
    //插入一组HomeMenu,onConflict是冲突解决方案,要插入的数据已存在就替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(list: List<HomeMenu>)

    //获取HomeMenu表所有数据并监听HomeMenu表
    @Query("select * from HomeMenu where typeId == :typeId")
    fun getHomeMenuList(typeId: String): List<HomeMenu>

    //获取HomeMenu表所有菜单类型的数据并监听HomeMenu表
    @Query("select * from HomeMenu where type == 0")
    fun getHomeMenuTypeList(): Flow<List<HomeMenu>>

    //清空HomeMenu表
    @Query("delete from HomeMenu")
    fun deleteAll()

    @Query("select * from HomeMenu")
    fun queryAll(): List<HomeMenu>
}