package com.sq26.experience.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sq26.experience.R
import kotlin.concurrent.thread

/**app的全局数据库
 * entities:包含的表
 * version:数据库的版本
 * exportSchema:是否将数据库导出到文件夹中
 */
@Database(
    entities = [DownloadEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    //dao层会自动生产
    abstract fun downloadDao(): DownloadDao

    companion object {
        //Volatile注解,申明线程安全(每次读取刷新cpu缓存)
        @Volatile
        private var instance: AppDatabase? = null

        //获取唯一单例
        fun getInstance(context: Context): AppDatabase {
            //如果instance是空就调用同步方法
            return instance ?: synchronized(this) {
                //如果instance还是空就创建数据库
                instance ?: Room.databaseBuilder(context, AppDatabase::class.java, "AppDatabase")
                    //添加回调
                    .addCallback(object : RoomDatabase.Callback() {
                        //创建数据库的回调
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                        }

                        //打开数据库的回调
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                        }
                    })
                    .addMigrations(MIGRATION_1_2())
                    .addMigrations(MIGRATION_2_3())
                    //构建
                    .build()
                    //also内联扩展函数,传入调用对象本身,返回调用对象本身,内部的lambda表达式处理对象的初始属性设置和赋值等操作
                    .also {
                        instance = it
                    }
            }
        }

        private fun MIGRATION_1_2(): Migration {
            //从1升级到2加了一个字段
            return object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    //在这里添加版本变化后所需要执行的sql语句
                    //创建零时表,所有的字段必须是not null
                    database.execSQL("create table new_RecyclerViewItem(id INTEGER primary key not null,sort INTEGER not null,color INTEGER not null)")
                    //将旧表数据加入新表,要与新表的字段数量对应
                    database.execSQL("insert into new_RecyclerViewItem select id,id,color from RecyclerViewItem")
                    //删除旧表
                    database.execSQL("drop table RecyclerViewItem")
                    //将新表重命名成旧表
                    database.execSQL("ALTER TABLE new_RecyclerViewItem RENAME TO RecyclerViewItem")
                    //直接添加新值
//                    database.execSQL("ALTER TABLE new_RecyclerViewItem add column sort INTEGER")

                }
            }
        }

        private fun MIGRATION_2_3(): Migration {
            //从2升级到3加了张表
            return object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    //创建表
                    //PRIMARY KEY(uri)将uri设置为主键，NOT NULL设置对应的键不能为空
                    database.execSQL(
                        "create table if not exists FileRoot(" +
                                "name Text not null," +
                                "rom Text not null," +
                                "uri Text not null," +
                                "canRead INTEGER not null," +
                                "canWrite INTEGER not null," +
                                "PRIMARY KEY(uri))"
                    )
                }
            }
        }
    }
}