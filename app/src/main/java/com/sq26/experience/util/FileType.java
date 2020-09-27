package com.sq26.experience.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class FileType {
    public static HashMap<String, String> fileTypes = new HashMap<String, String>();

    static {
        fileTypes.put("FFD8FF", "jpg");
        fileTypes.put("89504E47", "png");
        fileTypes.put("47494638", "gif");
        fileTypes.put("49492A00", "tif");
        fileTypes.put("424D", "bmp");
        fileTypes.put("41433130", "dwg"); //CAD
        fileTypes.put("38425053", "psd");
        fileTypes.put("7B5C727466", "rtf"); //日记本
        fileTypes.put("3C3F786D6C", "xml");
        fileTypes.put("68746D6C3E", "html");
        fileTypes.put("44656C69766572792D646174653A", "eml"); //邮件
        fileTypes.put("D0CF11E0", "doc");
        fileTypes.put("5374616E64617264204A", "mdb");
        fileTypes.put("252150532D41646F6265", "ps");
        fileTypes.put("255044462D312E", "pdf");
        fileTypes.put("504B0304", "zip");
        fileTypes.put("52617221", "rar");
        fileTypes.put("57415645", "wav");
        fileTypes.put("41564920", "avi");
        fileTypes.put("2E524D46", "rm");
        fileTypes.put("000001BA", "mpg");
        fileTypes.put("000001B3", "mpg");
        fileTypes.put("6D6F6F76", "mov");
        fileTypes.put("3026B2758E66CF11", "asf");
        fileTypes.put("4D546864", "mid");
        fileTypes.put("1F8B08", "gz");
    }
    //获取文件的真实文件类型
    public static String getFileType(String filePath) {
        String fileHeader = getFileHeader(filePath);
        for (String key : fileTypes.keySet()) {
            if (fileHeader.substring(0, key.length()).equals(key)) {
                return fileTypes.get(key);
            }
        }
        return "";
    }
    //判断文件是否是指定的类型
    public static boolean isAssignFileType(String filePath,String suffix) {
        String fileHeader = getFileHeader(filePath);
        if (fileHeader.substring(0,suffix.length()).equals(fileTypes.get(suffix))){
            return true;
        }else{
            return false;
        }
    }

    //获取文件头信息
    public static String getFileHeader(String filePath) {
        FileInputStream fileInputStream = null;
        String value = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            byte[] bytes = new byte[14];
            fileInputStream.read(bytes, 0, bytes.length);
            value = bytesToHexString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    //byte[]转16进制string
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (byte b : src) {
            hv = Integer.toHexString(b & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }
}
