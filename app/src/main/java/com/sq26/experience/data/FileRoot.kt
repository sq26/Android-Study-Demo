package com.sq26.experience.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "FileRoot")
data class FileRoot(
    //分区名称
    val name: String,
    //总大小
    val rom: String,
    //路径
    @PrimaryKey
    val uri: String,
    //是否有读权限
    val canRead: Boolean,
    //是否有写权限
    val canWrite: Boolean,
)

@Dao
interface FileRootDao {
    //OnConflictStrategy.ABORT：冲突策略是终止事务。
    //OnConflictStrategy.IGNORE：冲突策略是忽略冲突。
    //OnConflictStrategy.REPLACE：冲突策略是取代旧数据同时继续事务。
    //插入数据
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(fileRoot: FileRoot)

    //查询所有
    @Query("select * from FileRoot")
    fun queryAll(): Flow<List<FileRoot>>


}
