package com.sq26.experience.util.permissions;

import java.text.DecimalFormat;

//app工具类
public class AppUtils {
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
}
