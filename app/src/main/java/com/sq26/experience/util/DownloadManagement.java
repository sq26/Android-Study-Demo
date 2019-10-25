package com.sq26.experience.util;

import android.content.Context;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import com.sq26.experience.util.media.JImage;

import java.net.URL;

public class DownloadManagement {

    //初始化,创建构造器
    public static Builder initialize(Context context) {
        Builder builder = new Builder(context);
        return builder;
    }

    //初始化,创建构造器
    public static Builder initialize(Context context, String url) {
        Builder builder = new Builder(context, url);
        return builder;
    }

    //构造器类
    public static class Builder {
        private Context context;
        private String url;

        //构建
        public Builder(Context context) {
            this.context = context;
        }

        //构建
        public Builder(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        public Builder start() {


            return this;
        }


    }
}
