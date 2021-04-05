package com.sq26.experience.data

import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.room.*
import com.sq26.experience.util.network.download.DownloadStatus
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "download")
data class DownloadEntity(
    //设置主键(并设置自动增长)
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    //下载地址
    //此注解用于指向数据库对应字段名
//        @ColumnInfo(name = "url")
    val url: String = "",
    //文件的url路径
    var fileUri: String = "",
    //保存的文件名
    var fileName: String = "",
    //下载状态
    var status: Int = DownloadStatus.CREATE,
    //文件大小
    var size: Long = 0,
    //已下载大小
    var alreadySize: Long = 0,
    //错误次数
    var errorFrequency: Int = 0,
    //是否暂停
    var pauseFlag: Boolean = false,
    //是否删除
    var deleteFlag: Boolean = false,
    //是否保留文件
    var isKeepFile: Boolean = true
)

object DownloadEntityDiffCallback : DiffUtil.ItemCallback<DownloadEntity>() {
    override fun areItemsTheSame(oldItem: DownloadEntity, newItem: DownloadEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DownloadEntity, newItem: DownloadEntity): Boolean {
        return oldItem == newItem
    }

}

@Dao
interface DownloadDao {
    //查询所有
    @Query("select * from download")
    fun queryAllList(): Flow<List<DownloadEntity>>

    //查询所有
    @Query("select * from download")
    fun getAllDownloadList(): LiveData<List<DownloadEntity>>

    //查询所有未完成下载
    @Query("select * from download where status in(${DownloadStatus.WAIT},${DownloadStatus.AUTO_PAUSE})")
    fun getUndoneDownloadList(): List<DownloadEntity>

    //查询所有未初始化下载
    @Query("select * from download where status in(${DownloadStatus.CREATE})")
    fun getUndoneInitDownloadList(): List<DownloadEntity>

    //查询指定id的下载记录
    @Query("select * from download where id == :id")
    fun getDownloadForId(id: Long): DownloadEntity

    //查询指定id的下载记录
    @Query("select * from download where id == :id")
    fun queryItemForId(id: Long): Flow<DownloadEntity>

    //查询对应url的数据
    @Query("select * from download where url == :url")
    fun getDownloadForUrl(url: String): DownloadEntity

    //查询对应状态的数据数量
    @Query("select count(1) from download where status == :status")
    fun getDownloadForStatus(status: Int): Int

    //查询对应文件名称,路径,连接的数据
    @Query("select * from download where fileName == :fileName and fileUri == :fileUri")
    fun getDownloadForUrlAndFileNameAndDirPath(fileName: String, fileUri: String): DownloadEntity

    //新增一条
    @Insert
    fun addItem(item: DownloadEntity)

    //修改一条数据
    @Update
    fun updateItem(item: DownloadEntity)

    //新增一组
    @Insert
    fun addAllItem(mutableList: MutableList<DownloadEntity>)

    //删全部
    @Query("DELETE FROM download")
    fun deleteAll()


}