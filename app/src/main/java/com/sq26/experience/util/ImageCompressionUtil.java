package com.sq26.experience.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.util.Log;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * 图片压缩工具
 */
public class ImageCompressionUtil {
    //压缩方法,传入共享图片的uri,压缩并转换为file路径
    //reqHeight和reqWidth是期望的高宽度,小于这个高或宽不压缩图片
    public static String startCompression(Context context, Uri uri, int reqHeight, int reqWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //把inJustDecodeBounds设置为true,可以不把图片读到内存中,但依然可以计算出图片的大小
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //图片的高度
        int height = options.outHeight;
        //图片的宽度
        int width = options.outWidth;
        //压缩比
        int inSampleSize;
        //是否竖型图,默认否
        boolean isVertical = false;
        //判断是否是竖型图
        if (height > width)
            //是
            isVertical = true;
        //获取当前的时间戳
        long timeStamp = System.currentTimeMillis();
        //以时间戳做文件名,在app的缓存目录创建文件(缓存满后系统会自动清理其他应用的缓存,也可以在每次图片上传完后手动清缓存)
        File file = new File(context.getExternalCacheDir(), timeStamp + ".jpg");
        try {
            //文件基本不可能存在,所以直接创建文件
            if (file.createNewFile())
                //判断是否需要压缩,高度或宽度有任意一项大于设定的标准就进行压缩,通过判断是否竖型图,来调整对比对象(是竖型图就判断图片高度是否大于预期高度,
                // 不是竖型图就判断图片高度是否大于预期宽度,宽度判断同高度判断)
                if (height > (isVertical ? reqHeight : reqWidth) || width > (isVertical ? reqWidth : reqHeight)) {
                    //计算高度分辨率缩放比例
                    final int heightRatio = Math.round((float) height / (float) (isVertical ? reqHeight : reqWidth));
                    //计算宽度分辨率缩放比例
                    final int widthRatio = Math.round((float) width / (float) (isVertical ? reqWidth : reqHeight));
                    //计算高度和宽度的分辨率缩放比例,取较小的分辨率缩放比例
                    inSampleSize = Math.min(heightRatio, widthRatio);
                    Log.d("inSampleSize", inSampleSize + "");
                    //设置分辨率缩放比例
                    options.inSampleSize = inSampleSize;
                    //设置将图片读入内存
                    options.inJustDecodeBounds = false;
                    //获取新比例的图片的Bitmap对象
                    Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
                    if (bitmap != null) {
                        //创建ByteArrayOutputStream输出流
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        //将Bitmap设置为jpg格式(需要透明图片就设置成png),将质量设置到60,输出到ByteArrayOutputStream
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
                        //创建新文件的输出流
                        OutputStream outputStream = new FileOutputStream(file);
                        //把ByteArrayOutputStream输出流拷贝到outputStream输出流(把压缩后的新图片字节流写入到文件)
                        byteArrayOutputStream.writeTo(outputStream);
                        //关闭outputStream输出流
                        outputStream.close();
                        //关闭byteArrayOutputStream输出流
                        byteArrayOutputStream.close();
                        //返回压缩后的新文件路径
                        return file.getAbsolutePath();
                    } else {
                        //压缩失败返回空字符
                        return "";
                    }
                } else {
                    //判段不需要压缩,就直接将原图片路径返回
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        FileUtils.copy(Objects.requireNonNull(context.getContentResolver().openInputStream(uri)), new FileOutputStream(file));
                    } else {
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
                        in.close();
                        out.close();
                    }
                    //返回临时文件地址
                    return file.getAbsolutePath();
                }
            else
                //压缩失败返回空字符
                return "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        //压缩失败返回空字符
        return "";
    }
    //简单调用
    public static String startCompression(Context context, Uri uri) {
        //期望的宽高分别设定为1280,720,小于这个高宽不压缩图片
        return startCompression(context, uri, 1920, 1080);
    }
}
