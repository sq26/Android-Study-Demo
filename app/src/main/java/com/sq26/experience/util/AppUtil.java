package com.sq26.experience.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AppUtil {

    /**
     * 弹出基础Toast提示框
     *
     * @param context 上下文
     * @param resId   显示文本的resId
     */
    public static void showToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 读取私有文件数据
     *
     * @param context  上下文
     * @param fileName 文件名
     * @return 文件内容
     */
    public static String loadPrivateFile(Context context, String fileName) {
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            return new String(bytes);
        } catch (IOException e) {
            Log.d("读取私有文件","文件不存在");
        }
        return "";
    }

    /**
     * 保存数据到私有文件
     *
     * @param context  上下文
     * @param fileName 文件名
     * @param text     内容
     */
    public static void savePrivateFile(Context context, String fileName, String text) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(text.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
