package com.sq26.experience.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class UriToAbsolutePath {
    //上下文
    private Context context;
    //相对路径的文件uri
    private Uri uri;
    //指定要拷贝到的文件
    private File file;
    //转换后的文件是否是长期保留(true保存在app内部,默认保存在app缓存中)
    private boolean isLastingSave = false;

    UriToAbsolutePath(Context context) {
        this.context = context;
    }

    UriToAbsolutePath(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }

    UriToAbsolutePath(Context context, String uriString) {
        this.context = context;
        this.uri = Uri.parse(uriString);
    }

    public UriToAbsolutePath isLastingSave(boolean isLastingSave) {
        this.isLastingSave = isLastingSave;
        return this;
    }

    public UriToAbsolutePath uri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public UriToAbsolutePath uri(String uriString) {
        this.uri = Uri.parse(uriString);
        return this;
    }

    public UriToAbsolutePath file(File file) {
        this.file = file;
        return this;
    }

    public UriToAbsolutePath file(String fileString) {
        this.file = new File(fileString);
        return this;
    }

    public File returnFile() {
        uriToAbsolutePath();
        return file;
    }

    public String returnFileString() {
        uriToAbsolutePath();
        return file.getAbsolutePath();
    }

    /**
     * 将uri文件拷贝到指定位置获取绝对路径
     */
    public void uriToAbsolutePath() {
        try {
            //判断输出文件是否存在
            if (file == null) {
                //不存在就创建文件,以时间轴做父文件夹,能有效防止重名文件覆盖
                file = new File(isLastingSave ? context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) : context.getExternalCacheDir(), System.currentTimeMillis() + "");
                //创建时间轴文件夹
                if (file.mkdirs()) {
                    //以源文件的文件名,创建文件
                    file = new File(file, DocumentFile.fromSingleUri(context, uri).getName());
                    //创建新文件
                    file.createNewFile();
                }
            }
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
}
