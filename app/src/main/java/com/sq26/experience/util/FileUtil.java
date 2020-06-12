package com.sq26.experience.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import com.sq26.experience.BuildConfig;
import com.sq26.experience.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Objects;

//app工具类
public class FileUtil {
    //计算文件大小并加上相应单位
    //已弃用,Android 中提供的有单位转换类,可以使用Formatter.formatFileSize(Context context, long sizeBytes)方法获取
    public static String getFileSizeStr(Long size) {
        DecimalFormat df = new DecimalFormat("0.00");//设置保留位数
        String str;
        long d = 1024;
        //获取位数长度
        int ws = size.toString().length();
        if (ws > 12) {
            str = df.format((float) size / (d * d * d * d)) + "TB";
        } else if (ws > 9) {
            str = df.format((float) size / (d * d * d)) + "GB";
        } else if (ws > 6) {
            str = df.format((float) size / (d * d)) + "MB";
        } else if (ws > 3) {
            str = df.format((float) size / d) + "KB";
        } else {
            str = size + "B";
        }
        return str;
    }

    //根据文件路径获取文件名称(包含后缀)
    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    //根据文件路径获取文件后缀
    public static String getFileFormat(String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }

    //根据文件路径获取文件所在的文件夹路径
    public static String getFileParentFolderPath(String path) {
        return path.substring(0, path.lastIndexOf("/"));
    }

    //根据文件路径获取文件的父文件夹名称
    public static String getParentFileName(String path) {
        String[] s = path.split("/");
        return s[s.length - 2];
    }

    //根据文件路径判断文件是否存在,true代表存在
    public static boolean isFileExists(String path) {
        if (path != null && !path.isEmpty()) {
            File file = new File(path);
            return file.exists();
        } else {
            return false;
        }

    }

    //根据文件路径获取文件的mimeType类型
    public static String getMimeType(String path) {
        //获取文件后缀
        String format = getFileFormat(path);
        //创建通过单例获取到MimeTypeMap的对象。MimeTypeMap里保存着当前设备的Android版本所支持的文件类型所对应的MIME类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        //判断集合中是否存在该扩展名的类型。
        if (mimeTypeMap.hasExtension(format)) {
            //获取文件的扩展名的MIME类型。
            return mimeTypeMap.getMimeTypeFromExtension(format);
        } else {
            //不存在就返回空字符
            return "";
        }
    }


    public static void openFile(Context context, String path) {
        //根据文件路径path创建文件
        File file = new File(path);
        //定义文件url
        Uri uri;
        //判断系统是否大于等于Android7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            //大于等于Android7.0,需要把文件转换成私有相对路径
            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".FileProvider", file);
        else
            //小于Android7.0直接把file转换成uri
            uri = Uri.fromFile(file);
        openFile(context, uri);
    }

    //根据文件路径通过系统应用打开对应的文件
    public static void openFile(Context context, Uri uri) {
        //创建一个Intent并设置activity
        //ACTION_VIEW:用于显示用户的数据。比较通用，会根据用户的数据类型打开相应的Activity。
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //给intent添加旗帜,在Activity上下文之外启动Activity需要给Intent设置FLAG_ACTIVITY_NEW_TASK标志，不然会报异常。
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //设置文件路径和文件的MIME类型。
        intent.setDataAndType(uri, DocumentFile.fromSingleUri(context, uri).getType());
        //intent添加URI的临时访问权限声明
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //判断是否有可以打开此类文件的应用程序
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            //有,就启动
            context.startActivity(intent);
        } else {
            //没有,给出提示
            AppUtil.showToast(context, R.string.There_is_no_application_that_can_open_this_file);
        }
    }

    //判断一个文件路径是否是绝对路径
    public static boolean isAbsolutePath(String path) {
        if (path.contains("content://")) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 将uri文件拷贝到指定位置获取绝对路径
     *
     * @param context 上下文
     * @param uri     相对路径的文件uri
     * @param file    指定要拷贝到的文件
     */
    public static void uriToAbsolutePath(Context context, Uri uri, File file) {
        try {
            //创建输入流
            InputStream in = Objects.requireNonNull(context.getContentResolver().openInputStream(uri));
            //创建输出流
            OutputStream out = new FileOutputStream(file);
            //定义缓冲大小为2M
            int bytesSize = 1024 * 1024 * 2;
            byte[] buffer = new byte[bytesSize];
            //用于保存每次读取后的位子
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            //关闭输入流
            in.close();
            //关闭输出流
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //没指定拷贝位置的方法,需要返回拷贝完的File路径
    public static String uriToAbsolutePath(Context context, Uri uri) {
        //将文件拷贝在缓存里
        File file = new File(context.getExternalCacheDir(), Objects.requireNonNull(Objects.requireNonNull(DocumentFile.fromSingleUri(context, uri)).getName()));
        uriToAbsolutePath(context, uri, file);
        return file.getAbsolutePath();
    }

    //没指定拷贝位置的方法,传string,需要返回拷贝完的File路径
    public static String uriToAbsolutePath(Context context, String uriString) {
        return uriToAbsolutePath(context, Uri.parse(uriString));
    }

    //指定拷贝位置,路径是string的方法
    public static void uriToAbsolutePath(Context context, Uri uri, String path) {
        uriToAbsolutePath(context, uri, new File(path));
    }

    //指定拷贝位置,uri和路径都是string的方法
    public static void uriToAbsolutePath(Context context, String uriString, String path) {
        uriToAbsolutePath(context, Uri.parse(uriString), new File(path));
    }

    //指定拷贝位置,uri是string的方法
    public static void uriToAbsolutePath(Context context, String uriString, File file) {
        uriToAbsolutePath(context, Uri.parse(uriString), file);
    }
}
