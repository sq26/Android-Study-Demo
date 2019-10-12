package com.sq26.experience.util;

import java.text.DecimalFormat;

//app工具类
public class FileUtils {
    public static String getFileSizeStr(Long size) {
        DecimalFormat df = new DecimalFormat("0.00");//设置保留位数
        String str;
        long d = 1024;
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

    //根据文件路径获取文件所在的文件夹路径
    public static String getFileParentFolderPath(String path) {
        return path.substring(0, path.lastIndexOf("/"));
    }

    //根据文件路径获取文件的父文件夹名称
    public static String getParentFileName(String path) {
        String[] s = path.split("/");
        return s[s.length - 2];
    }
}
