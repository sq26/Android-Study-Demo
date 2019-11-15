package com.sq26.experience.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DownloadManagementHelper extends SQLiteOpenHelper {
    //最新的数据库版本
    public static final int VERSION = 1;
    //数据库的名称
    public static final String DATABASE_NAME = "downloadManagement";
    //下载列表的表名
    public static final String TABLE_DOWNLOAD_LIST = "t_download_list";

    /**
     * 下载管理库的构造器
     * context:上下文
     * name:数据库的名称
     * factory:游标,用来指向数据库里面的某一行
     * version:数据库的版本
     */
    public DownloadManagementHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //创建数据表,在第一次创建时调用
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建下载管理列表(在每次更新数据库时,要把创建语句修改为修改后的语句,
        // 第一次安装应用的手机不会去调用更新方法,走更新方法的手机也不会调用创建方法,所以这里的建表sql语句必须时最新版)
        String sql = "create table " + TABLE_DOWNLOAD_LIST + " (" +
                "id integer," +//下载id
                "url text," +//下载地址
                "path text" +//本地保存路径
                ")";
        //执行sql语句
        sqLiteDatabase.execSQL(sql);
    }

    //数据库的更新方法
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //当现在的数据库版本和调用传递的数据库版本不一致时调用
        if (oldVersion != newVersion) {
            //以下是示例,比如最新的数据库版本变为2,就进入判断
            if (oldVersion < 2){
                //在这里面执行所有数据库版本小于2数据库的更新sql
            }
            //这里示例小于数据库版本小于3的判断,这里给安装了数据库版本为2的手机跟新到最新库的判断
            //同时兼容了从1版数据库直接到3号版本
            if (oldVersion < 3){
                //在这里面执行所有数据库版本小于2数据库的更新sql
                //其实只用写从2号版本升级到3号本吧的sql就行了
            }
        }
    }
}
