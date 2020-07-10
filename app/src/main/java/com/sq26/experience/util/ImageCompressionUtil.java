package com.sq26.experience.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;

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
    //全局上下文
    private Context context;
    //期望高度(以竖屏为标准)
    private int reqHeight = 1920;
    //期望宽度(以竖屏为标准)
    private int reqWidth = 1080;
    //图片的uri
    private Uri uri;
    //图片的path
    private String path;
    //图片是否是绝对路径
    private boolean isAbsolutePath;
    //压缩后的图片是否是长期保留(true保存在app内部,默认保存在app缓存中)
    private boolean isLastingSave = false;
    //保存压缩后的图的格式
    private Bitmap.CompressFormat bitmapCompressFormat;
    //保存压缩后图片文件的后缀
    private String fileFormat;
    //返回得文件

    //初始化构造函数
    public ImageCompressionUtil(Context context) {
        this.context = context;
    }

    //设置高度
    public ImageCompressionUtil reqHeight(int reqHeight) {
        this.reqHeight = reqHeight;
        return this;
    }

    //设置宽度
    public ImageCompressionUtil reqWidth(int reqWidth) {
        this.reqWidth = reqWidth;
        return this;
    }

    //设置uri
    public ImageCompressionUtil uri(Uri uri) {
        this.uri = uri;
        //判断uri是不是绝对路径
        if (FileUtil.isAbsolutePath(uri.toString())) {
            this.isAbsolutePath = true;
            this.path = uri.toString();
        } else {
            this.isAbsolutePath = false;
        }
        this.fileFormat = FileUtil.getFileFormat(uri.toString()).toLowerCase();
        switch (fileFormat) {
            case "png":
                bitmapCompressFormat = Bitmap.CompressFormat.PNG;
                break;
            case "jpg":
            case "jpeg":
            default:
                //没有后缀默认取jpg
                bitmapCompressFormat = Bitmap.CompressFormat.JPEG;
                break;
        }
        return this;
    }

    //设置path
    public ImageCompressionUtil path(String path) {
        this.isAbsolutePath = true;
        this.path = path;
        //获取原图后缀
        this.fileFormat = FileUtil.getFileFormat(path).toLowerCase();
        switch (fileFormat) {
            case "png":
                bitmapCompressFormat = Bitmap.CompressFormat.PNG;
                break;
            case "jpg":
            case "jpeg":
            default:
                //没有后缀默认取jpg
                bitmapCompressFormat = Bitmap.CompressFormat.JPEG;
                break;
        }
        return this;
    }

    //开始压缩并返回string类型路径
    public String startCompressionToString() {
        return startCompression();
    }

    //开始压缩并返回Uri类型路径
    public Uri startCompressionToUri() {
        return Uri.parse(startCompression());
    }

    //压缩方法,传入共享图片的uri,压缩并转换为file路径
    //reqHeight和reqWidth是期望的高宽度,小于这个高或宽不压缩图片
    private String startCompression() {
        //创建一个Bitmap工厂的设置
        BitmapFactory.Options options = new BitmapFactory.Options();
        //图片的高度
        int height;
        //图片的宽度
        int width;
        //用于保存新图片的Bitmap
        Bitmap bitmap = null;
        //获取图片角度
        int angle = readPictureDegree();
        //判断图片是否需要选择
        if (angle != 0) {
            //角度不是0,需要旋转
            //把inJustDecodeBounds设置为true,可以不把图片读到内存中,但依然可以计算出图片的大小
            options.inJustDecodeBounds = false;
            //获取原有的Bitmap
            Bitmap originalBitmap = null;
            //判断是否绝对路径
            if (isAbsolutePath) {
                //通过绝对路径获取originalBitmap
                originalBitmap = BitmapFactory.decodeFile(uri.toString(), options);
            } else {
                try {
                    //通过inputStream获取originalBitmap
                    originalBitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            //申明矢量
            Matrix matrix = new Matrix();
            //设置旋转角度
            matrix.postRotate(angle);
            //创建矫正后的Bitmap
            bitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
            //图片的高度
            height = bitmap.getHeight();
            //图片的宽度
            width = bitmap.getWidth();
        } else {
            //角度等于0,不需要旋转
            //把inJustDecodeBounds设置为true,可以不把图片读到内存中,但依然可以计算出图片的大小
            options.inJustDecodeBounds = true;
            //判断是否绝对路径
            if (isAbsolutePath) {
                //通过文件获取配置
                BitmapFactory.decodeFile(path, options);
            } else {
                try {
                    //通过inputStream获取获取配置
                    BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            //图片的高度
            height = options.outHeight;
            //图片的宽度
            width = options.outWidth;
        }
        //压缩比
        int inSampleSize;
        //是否竖型图,默认否
        boolean isVertical = false;
        //判断是否是竖型图
        if (height > width)
            //是
            isVertical = true;
        //判断是否需要压缩,高度或宽度有任意一项大于设定的标准就进行压缩,通过判断是否竖型图,来调整对比对象(是竖型图就判断图片高度是否大于预期高度,
        // 不是竖型图就判断图片高度是否大于预期宽度,宽度判断同高度判断)
        if (height > (isVertical ? reqHeight : reqWidth) || width > (isVertical ? reqWidth : reqHeight)) {
            //计算高度分辨率缩放比例
            final int heightRatio = Math.round((float) height / (float) (isVertical ? reqHeight : reqWidth));
            //计算宽度分辨率缩放比例
            final int widthRatio = Math.round((float) width / (float) (isVertical ? reqWidth : reqHeight));
            //计算高度和宽度的分辨率缩放比例,取较小的分辨率缩放比例
            inSampleSize = Math.min(heightRatio, widthRatio);
            //设置分辨率缩放比例
            options.inSampleSize = inSampleSize;
            //设置将图片读入内存
            options.inJustDecodeBounds = false;
            //判断是否是需要旋转的图
            if (bitmap != null) {
                //不等空说明命是需要旋转的图
                //获取当前的时间戳
                long timeStamp = System.currentTimeMillis();
                //以时间戳做文件名,在app的缓存目录创建文件(缓存满后系统会自动清理其他应用的缓存,也可以在每次图片上传完后手动清缓存)
                File file = new File(context.getExternalCacheDir(), timeStamp + fileFormat);
                try {
                    //文件基本不可能存在,所以直接创建文件
                    if (file.createNewFile()) {
                        //创建ByteArrayOutputStream输出流
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        //将Bitmap设置为原图获取的格式,将质量设置到100表示不压缩,输出到ByteArrayOutputStream
                        bitmap.compress(bitmapCompressFormat, 100, byteArrayOutputStream);
                        //创建新文件的输出流
                        OutputStream outputStream = new FileOutputStream(file);
                        //把ByteArrayOutputStream输出流拷贝到outputStream输出流(把压缩后的新图片字节流写入到文件)
                        byteArrayOutputStream.writeTo(outputStream);
                        //关闭outputStream输出流
                        outputStream.close();
                        //关闭byteArrayOutputStream输出流
                        byteArrayOutputStream.close();
                        //释放bitmap占用的C内存空间的内存
                        bitmap.recycle();
                        //获取新比例的图片的Bitmap对象
                        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //等空说明命是不需要旋转的图
                //判断是否是绝对路径
                if (isAbsolutePath) {
                    //获取新比例的图片的Bitmap对象,通过path
                    bitmap = BitmapFactory.decodeFile(path, options);
                } else {
                    try {
                        //获取新比例的图片的Bitmap对象,通过uri
                        bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            //获取当前的时间戳
            long timeStamp = System.currentTimeMillis();
            //以时间戳做文件名,通过isLastingSave判断保存在app的缓存里还是app文件目录里(缓存满后系统会自动清理其他应用的缓存,也可以在每次图片上传完后手动清缓存)
            File file = new File(isLastingSave ? context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) : context.getExternalCacheDir(),
                    timeStamp + fileFormat);
            //bitmap 不等空说明图片缩放成功
            if (bitmap != null) {
                try {
                    //创建文件
                    if (file.createNewFile()) {
                        //创建ByteArrayOutputStream输出流
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        //将Bitmap设置为jpg格式(需要透明图片就设置成png),将质量设置到60,输出到ByteArrayOutputStream
                        bitmap.compress(bitmapCompressFormat, 60, byteArrayOutputStream);
                        //创建新文件的输出流
                        OutputStream outputStream = new FileOutputStream(file);
                        //把ByteArrayOutputStream输出流拷贝到outputStream输出流(把压缩后的新图片字节流写入到文件)
                        byteArrayOutputStream.writeTo(outputStream);
                        //关闭outputStream输出流
                        outputStream.close();
                        //关闭byteArrayOutputStream输出流
                        byteArrayOutputStream.close();
                        //释放bitmap占用的C内存空间的内存
                        bitmap.recycle();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //返回新的图片的路径
            return file.getAbsolutePath();
        } else {
            //判段不需要压缩,就直接将原图片路径返回
            if (isAbsolutePath) {
                return path;
            } else {
                return uri.toString();
            }
        }
    }


    private int readPictureDegree() {
        //声明对象保存图片角度,存值包括0,90,180,270
        int degree = 0;
        try {
            //申明ExifInterface,它是保存照片信息的类
            ExifInterface exifInterface;
            //判断是不是绝对路径
            if (isAbsolutePath) {
                //绝对路径可以直接设置
                exifInterface = new ExifInterface(path);
            } else {
                //相对路径,通过转换成绝对路径来获取
                //使用其他的构造方法对Android版本有要求,这个是折中处理方法
                exifInterface = new ExifInterface(FileUtil.uriToAbsolutePath(context, path));
            }
            //getAttributeInt:返回指定标签的整数值。
            //TAG_ORIENTATION:按行和列查看的图像方向。
            //ORIENTATION_NORMAL:当前的图片方向
            int ORIENTATION = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            //获取ORIENTATION类型对应的角度
            switch (ORIENTATION) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

}
