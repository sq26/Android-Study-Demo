package com.sq26.experience.util.network.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSONObject;
import com.sq26.experience.database.DownloadHelper;

import java.util.HashMap;
import java.util.Map;


public class DownloadHelperUtil {
    /**
     * 新增一条下载记录,返回true表示成功
     */
    public static boolean insert(Context context, String url, String path) {
        //判断这条url,是否存在,存在就不进行插入动作(预防重复调用,造成重复数据)
        if (query(context, url) != null)
            return false;
        //创建或获取下载管理数据库
        DownloadHelper downloadHelper =
                new DownloadHelper(context, DownloadHelper.DATABASE_NAME, null, DownloadHelper.VERSION);
        //打开数据库
        SQLiteDatabase database = downloadHelper.getWritableDatabase();
        //创建ContentValues ,ContentValues 是一种存储的机制,只能存储基本类型的数据，像string，int之类的，不能存储对象这种东西
        ContentValues contentValues = new ContentValues();
        //插入网络链接
        contentValues.put("url", url);
        //插入到目前为止下载的字节数
        contentValues.put("column_bytes", "0");
        //插入网络状态(默认状态)
        contentValues.put("status", Download.STATUS_START);
        //插入保存路径
        contentValues.put("path", path);
        /**
         * 执行数据插入方法,获取到改变的行数
         * table:数据表的名称
         * nullColumnHack:代表强行插入null值的数据列的列名。contentValues的key为空时,指定一个列的列名
         * ContentValues:数据集合
         */
        long l = database.insert(DownloadHelper.TABLE_DOWNLOAD_LIST, null, contentValues);
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
        DownloadHelper downloadHelper =
                new DownloadHelper(context, DownloadHelper.DATABASE_NAME, null, DownloadHelper.VERSION);
        //打开数据库
        SQLiteDatabase database = downloadHelper.getWritableDatabase();
        /**
         * 执行数据删除方法,获取到改变的行数
         * table:数据表的名称
         * whereClause:满足该判断语句的记录将会被删除。
         * whereArgs:用于为whereClause子句传入参数。
         */
        long l = database.delete(DownloadHelper.TABLE_DOWNLOAD_LIST, "id = ?", new String[]{id + ""});
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
        DownloadHelper downloadHelper =
                new DownloadHelper(context, DownloadHelper.DATABASE_NAME, null, DownloadHelper.VERSION);
        //打开数据库
        SQLiteDatabase database = downloadHelper.getWritableDatabase();
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
        long l = database.update(DownloadHelper.TABLE_DOWNLOAD_LIST, contentValues, "url = ?", new String[]{url});
        //关闭数据库
        database.close();
        //行数为1说明修改成功
        return l == 1;
    }

    /**
     * 修改一条下载记录,返回true表示成功
     */
    public static boolean updateStatus(Context context, String url, int status) {
        //创建或获取下载管理数据库
        DownloadHelper downloadHelper =
                new DownloadHelper(context, DownloadHelper.DATABASE_NAME, null, DownloadHelper.VERSION);
        //打开数据库
        SQLiteDatabase database = downloadHelper.getWritableDatabase();
        //创建ContentValues
        ContentValues contentValues = new ContentValues();
        //设置新的path
        contentValues.put("status", status);
        /**
         * 执行数据更新方法,获取到改变的行数
         * table:数据表的名称
         * values:代表想要更新的数据。
         * whereClause:满足该判断语句的记录将会被更新。
         * whereArgs:用于为whereClause语句中的?传递参数。
         */
        long l = database.update(DownloadHelper.TABLE_DOWNLOAD_LIST, contentValues, "url = ?", new String[]{url});
        //关闭数据库
        database.close();
        //行数为1说明修改成功
        return l == 1;
    }

    public static Map query(Context context, String url) {
//创建或获取下载管理数据库
        DownloadHelper downloadHelper =
                new DownloadHelper(context, DownloadHelper.DATABASE_NAME, null, DownloadHelper.VERSION);
        //打开数据库
        SQLiteDatabase database = downloadHelper.getWritableDatabase();
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
        Cursor cursor = database.query(DownloadHelper.TABLE_DOWNLOAD_LIST, null, "url = ?", new String[]{url}, null, null, null);
        //定义Map集合
        Map<String, Object> map = null;
        //这里只可能有一条数据,下载id,下载链接,文件路径全部都是唯一的,不可能有多条
        if (cursor.getCount() > 0) {
            //把游标移到第一位
            cursor.moveToPosition(0);
            //创建HashMap集合
            map = new HashMap<>();
            //获取路径
            map.put("url", cursor.getString(cursor.getColumnIndex("url")));
            //获取到目前为止下载的字节数
            map.put("column_bytes", cursor.getString(cursor.getColumnIndex("column_bytes")));
            //获取下载的总大小
            map.put("column_total", cursor.getString(cursor.getColumnIndex("column_total")));
            //获取状态
            map.put("status", cursor.getInt(cursor.getColumnIndex("status")));
            //获取路径
            map.put("path", cursor.getString(cursor.getColumnIndex("path")));
        }
        //关闭游标
        cursor.close();
        //关闭数据库
        database.close();
        return map;
    }
}
