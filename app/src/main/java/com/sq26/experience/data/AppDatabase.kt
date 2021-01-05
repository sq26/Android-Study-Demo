package com.sq26.experience.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sq26.experience.R
import kotlin.concurrent.thread

/**app的全局数据库
 * entities:包含的表
 * version:数据库的版本
 * exportSchema:是否将数据库导出到文件夹中
 */
@Database(entities = [HomeMenu::class, HomeMenuType::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homeMenuDao(): HomeMenuDao
    abstract fun homeMenuTypeDao(): HomeMenuTypeDao

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
                            //初始化首页功能菜单
                            initHomeMenu(getInstance(context))
                        }
                    })
                    //构建
                    .build()
                    //also内联扩展函数,传入调用对象本身,返回调用对象本身,内部的lambda表达式处理对象的初始属性设置和赋值等操作
                    .also {
                        instance = it
                    }
            }
        }

        fun initHomeMenu(appDatabase: AppDatabase) {
            thread {
                val homeMenuTypeList = mutableListOf<HomeMenuType>()
                homeMenuTypeList.add(HomeMenuType(0, "技术功能"))
                homeMenuTypeList.add(HomeMenuType(1, "android功能"))
                homeMenuTypeList.add(HomeMenuType(2, "控件功能"))
                appDatabase.homeMenuTypeDao().insertAll(homeMenuTypeList)

                appDatabase.homeMenuDao().deleteAll()
                val homeMenuList = mutableListOf<HomeMenu>()
                homeMenuList.add(HomeMenu("encryption", "对称和非对称加密", 0))
                homeMenuList.add(HomeMenu("aidl", "AIDL进程间通讯", 0))
                homeMenuList.add(HomeMenu("Navigation", "导航框架", 1))
                appDatabase.homeMenuDao().insertAll(homeMenuList)
            }
        }
    }

}