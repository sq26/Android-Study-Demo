package com.sq26.experience.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 图片压缩工具
 */
public class ImageCompressionUtil {
    //压缩方法,把压缩好的图片的路径返回
    public static String startCompression(Context context, String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //把inJustDecodeBounds设置为true,可以不把图片读到内存中,但依然可以计算出图片的大小
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //图片的高度
        int height = options.outHeight;
        //图片的宽度
        int width = options.outWidth;
        //压缩比
        int inSampleSize = 1;
        //期望的宽高分别设定为1280,720,小于这个高宽不压缩图片
        int reqHeight = 1280;
        int reqWidth = 720;
        //是否竖型图,默认否
        boolean isVertical = false;
        //判断是否是竖型图
        if (height > width)
            //是
            isVertical = true;
        if (height > (isVertical ? reqHeight : reqWidth) || width > (isVertical ? reqWidth : reqHeight)) {
            final int heightRatio = Math.round((float) height / (float) (isVertical ? reqHeight : reqWidth));
            final int widthRatio = Math.round((float) width / (float) (isVertical ? reqWidth : reqHeight));
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        } else {
            return path;
        }
        Log.d("inSampleSize",inSampleSize+"");
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        long timeStamp = System.currentTimeMillis();
        File file = new File(context.getExternalCacheDir(), timeStamp + ".jpg");
        try {
            if (!file.exists())
                file.createNewFile();
            OutputStream outputStream = new FileOutputStream(file);
            byteArrayOutputStream.writeTo(outputStream);
            outputStream.close();
            byteArrayOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
