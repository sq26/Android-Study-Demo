package com.sq26.experience.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSONObject;
import com.sq26.experience.database.DownloadManagementHelper;


public class DownloadManagementHelperUtil {
    /**
     * 新增一条下载记录,返回true表示成功
     */
    public static boolean insert(Context context, String url) {
        //创建或获取下载管理数据库
        DownloadManagementHelper downloadManagementHelper =
                new DownloadManagementHelper(context, DownloadManagementHelper.DATABASE_NAME, null, DownloadManagementHelper.VERSION);
        //打开数据库
        SQLiteDatabase database = downloadManagementHelper.getWritableDatabase();
        //创建ContentValues ,ContentValues 是一种存储的机制,只能存储基本类型的数据，像string，int之类的，不能存储对象这种东西
        ContentValues contentValues = new ContentValues();
        //插入网络链接
        contentValues.put("url", url);
        /**
         * 执行数据插入方法,获取到改变的行数
         * table:数据表的名称
         * nullColumnHack:代表强行插入null值的数据列的列名。contentValues的key为空时,指定一个列的列名
         * ContentValues:数据集合
         */
        long l = database.insert(DownloadManagementHelper.TABLE_DOWNLOAD_LIST, null, contentValues);
        //关闭数据库
        database.close();
        //行数为1说明插入成功
        return l == 1;
    }

    /**
     * 删除一条下载记录,返回true表示成功
     */
    public static boolean delete(Context context, long id) {
        //创建或获取下载管理数据库
        DownloadManagementHelper downloadManagementHelper =
                new DownloadManagementHelper(context, DownloadManagementHelper.DATABASE_NAME, null, DownloadManagementHelper.VERSION);
        //打开数据库
        SQLiteDatabase database = downloadManagementHelper.getWritableDatabase();
        /**
         * 执行数据删除方法,获取到改变的行数
         * table:数据表的名称
         * whereClause:满足该判断语句的记录将会被删除。
         * whereArgs:用于为whereClause子句传入参数。
         */
        long l = database.delete(DownloadManagementHelper.TABLE_DOWNLOAD_LIST, "id = ?", new String[]{id + ""});
        //关闭数据库
        database.close();
        //行数为1说明删除成功
        return l == 1;
    }

    /**
     * 修改一条下载记录,返回true表示成功
     */
    public static boolean update(Context context, String url, long id, String path) {
        //创建或获取下载管理数据库
        DownloadManagementHelper downloadManagementHelper =
                new DownloadManagementHelper(context, DownloadManagementHelper.DATABASE_NAME, null, DownloadManagementHelper.VERSION);
        //打开数据库
        SQLiteDatabase database = downloadManagementHelper.getWritableDatabase();
        //创建ContentValues
        ContentValues contentValues = new ContentValues();
        //设置新的id
        contentValues.put("id", id);
        //设置新的path
        contentValues.put("path", path);
        /**
         * 执行数据更新方法,获取到改变的行数
         * table:数据表的名称
         * values:代表想要更新的数据。
         * whereClause:满足该判断语句的记录将会被更新。
         * whereArgs:用于为whereClause语句中的?传递参数。
         */
        long l = database.update(DownloadManagementHelper.TABLE_DOWNLOAD_LIST, contentValues, "url = ?", new String[]{url});
        //关闭数据库
        database.close();
        //行数为1说明修改成功
        return l == 1;
    }


    public static JSONObject query(Context context, String url) {
//创建或获取下载管理数据库
        DownloadManagementHelper downloadManagementHelper =
                new DownloadManagementHelper(context, DownloadManagementHelper.DATABASE_NAME, null, DownloadManagementHelper.VERSION);
        //打开数据库
        SQLiteDatabase database = downloadManagementHelper.getWritableDatabase();
        /**
         * 执行数据查询方法,获取到Cursor游标
         * distinct:指定是否去除重复记录。
         * table:执行查询数据的表名。
         * columns:要查询出来的列名。
         * selection:查询条件子句。
         *selectionArgs:用于为selection语句中的?传递参数。
         * groupBy:用于控制分组
         * having:用于对分组进行过滤。
         * orderBy:用于对记录进行排序。
         * limit:用于进行分页。
         */
        Cursor cursor = database.query(DownloadManagementHelper.TABLE_DOWNLOAD_LIST, null, "url = ?", new String[]{url}, null, null, null);
        //定义jsonObject集合
        JSONObject jsonObject = null;
        //这里只可能有一条数据,下载id,下载链接,文件路径全部都是唯一的,不可能有多条
        if (cursor.getCount() > 0) {
            //把游标移到第一位
            cursor.moveToPosition(0);
            //创建jsonObject集合
            jsonObject = new JSONObject();
            //获取id
            jsonObject.put("id", cursor.getLong(cursor.getColumnIndex("id")));
            //获取路径
            jsonObject.put("path", cursor.getString(cursor.getColumnIndex("path")));
        }
        //关闭游标
        cursor.close();
        //关闭数据库
        database.close();
        return jsonObject;
    }
}
