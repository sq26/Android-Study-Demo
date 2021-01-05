package com.sq26.experience.data

import androidx.room.*

@Entity(tableName = "HomeMenuType")
data class HomeMenuType(
    @PrimaryKey
    val id: Int,
    val name: String
)

@Dao
interface HomeMenuTypeDao {
    @Update
    fun insertAll(list: List<HomeMenuType>)

    @Query("select * from HomeMenuType")
    fun getAllList(): List<HomeMenuType>
}