package com.sq26.androidstudydemo.util;

import android.util.Log;

public class StringToAscii {
    //将字符串转成ASCII的Java方法
    public static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();
    }

    //将字符串转成ASCII16位的Java方法
    public static String stringToAscii16(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append(Integer.toHexString(chars[i])).append(",");
            } else {
                sbu.append(Integer.toHexString(chars[i]));
            }
        }
        return sbu.toString();
    }

    //将字符串转成ASCII16位byte数组的Java方法
    public static byte[] stringToAscii16byte(String value) {
        char[] chars = value.toCharArray();
        byte[] bytes = new byte[chars.length];

        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte) Integer.parseInt(Integer.toHexString(chars[i]), 16);
        }
        return bytes;
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    /**
     * 字节数组转10进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToString10(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i]);
//            if (hex.length() < 2) {
//                sb.append(0);
//            }
            sb.append(hex);
        }
        return sb.toString();
    }
    //把16位hex转为10位int
    public static int HexToInt10(String hex) {
        int i;
        int s1 = Integer.parseInt(hex.substring(0, 1), 16);
        int s2 = Integer.parseInt(hex.substring(1, 2), 16);
        i = s1 * 16 + s2;
        return i;
    }

    //将ASCII转成字符串的java方法
    public static String asciiToString(String value) {
        if (value.isEmpty()) {
            return "";
        }
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }
}
