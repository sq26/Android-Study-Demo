package com.sq26.experience.util.network.download

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.concurrent.Flow

@Entity(tableName = "download")
data class DownloadEntity(
        //设置主键(并设置自动增长)
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null,
        //下载地址
        //此注解用于指向数据库对应字段名
//        @ColumnInfo(name = "url")
        var url: String = "",
        //文件的url路径
        var fileUri: String = "",
        //保存的文件名
        var fileName: String = "",
        //下载状态，
        var status: Int = 0,
        //文件大小
        var size: Long = 0
)

@Dao
interface DownloadDao {
    //查询所有
    @Query("select * from download")
    fun getAllDownloadList(): LiveData<List<DownloadEntity>>

    //查询所有未完成下载
    @Query("select * from download where status in(1,3)")
    fun getUndoneDownloadList(): MutableList<DownloadEntity>

    //查询指定id的下载记录
    @Query("select * from download where id == :id")
    fun getDownloadForId(id: Int): DownloadEntity

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
}

@Entity(tableName = "download_slice")
data class DownloadSliceEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null,
        //下载任务的Id
        var downloadId: Int = 0,
        //开始下标
        var startIndex: Long = 0,
        //结束下标
        var endIndex: Long = 0,
        //已下载大小
        var alreadySize: Long = 0,
        //下载状态,1已完成,2未完成,3下载中
        var status: Int = 2,
        //切片文件的位置
        var filePath: String = ""
)


@Dao
interface DownloadSliceDao {
    //查询所有
    @Query("select * from download_slice")
    fun getAllDownloadSliceList(): MutableList<DownloadSliceEntity>

    //根据downloadId获取列表
    @Query("select * from download_slice where downloadId == :downloadId")
    fun getListForDownloadId(downloadId: Int): MutableList<DownloadSliceEntity>

    //查询所有指定下载id和指定状态的列表
    @Query("select * from download_slice where downloadId == :downloadId and status == :status")
    fun getListForDownloadIdAndStatus(downloadId: Int, status: Int): MutableList<DownloadSliceEntity>

    //查询所有对应状态的数据数量
    @Query("select count(1) from download_slice where status == :status")
    fun getListSizeForStatus(status: Int): Int

    //查询所有对应状态的切片数量
    @Query("select count(1) from download_slice where downloadId == :downloadId and status == :status")
    fun getListSizeForDownloadIdAndStatus(downloadId: Int, status: Int): Int

    //查询所有对应状态的数据数量
    @Query("select sum(alreadySize) from download_slice where downloadId == :downloadId")
    fun getAlreadySizeTotal(downloadId: Int): Long

    //新增一条
    @Insert
    fun addItem(item: DownloadSliceEntity)

    //修改一条数据
    @Update
    fun updateItem(item: DownloadSliceEntity)

    //新增一组
    @Insert
    fun addAllItem(list: List<DownloadSliceEntity>)

    //新增一组
    @Delete
    fun deleteAllItem(list: List<DownloadSliceEntity>)
}

@Database(entities = [DownloadEntity::class, DownloadSliceEntity::class], version = 1)
abstract class DownloadDatabase : RoomDatabase() {
    //获取下载列表的dao层
    abstract fun getDownloadDao(): DownloadDao

    //获取下载切片列表的dao层
    abstract fun getDownloadSliceDao(): DownloadSliceDao

    //创建伴生类
    companion object {
        //申明线程安全(每次读取刷新cpu缓存)
        @Volatile
        private var instance: DownloadDatabase? = null

        //获取唯一单例
        fun getInstance(context: Context): DownloadDatabase {
            //使用线程同步
            synchronized(DownloadDatabase::class) {
                //判断是否创建过
                if (instance == null)
                //没创建过就创建
                    instance = Room.databaseBuilder(
                            context,
                            DownloadDatabase::class.java,
                            "DownloadDatabase.db"
                    )
//                            设置数据库的factory。比如我们想改变数据库的存储路径可以通过这个函数来实现
//                            .openHelperFactory()
//                            设置数据库升级(迁移)的逻辑
//                            .addMigrations()
//                            设置是否允许在主线程做查询操作
                            .allowMainThreadQueries()
//                            设置数据库的日志模式
//                            .setJournalMode()
//                            设置迁移数据库如果发生错误，将会重新创建数据库，而不是发生崩溃
//                            .fallbackToDestructiveMigration()
//                            设置从某个版本开始迁移数据库如果发生错误，将会重新创建数据库，而不是发生崩溃
//                            .fallbackToDestructiveMigrationFrom()
//                            监听数据库，创建和打开的操作
//                            .addCallback()
                            .build()
                //返回唯一单例
                return instance!!
            }
        }
    }
}