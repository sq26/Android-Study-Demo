package com.sq26.experience.util.media;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sq26.experience.R;


public class JImage {
    //全部
    public static final int ALL = 0;
    //拍照
    public static final int PHOTO = 1;
    //相册
    public static final int ALBUM = 2;

    //初始化,创建构造器
    public static Builder initialize(AppCompatActivity c) {
        Builder builder = new Builder(c);
        return builder;
    }

    //构造器类
    public static class Builder {
        private AppCompatActivity contents;
        private Integer sourceType;
        private SuccessCallback successCallback;
        private ImageFragment imageFragment;

        //构建
        public Builder(AppCompatActivity contents) {
            this.contents = contents;
        }

        //设置图片来源(从相册还是拍照)
        public Builder setImageSource(Integer sourceType) {
            this.sourceType = sourceType;
            return this;
        }

        //设置成功的回调
        public Builder success(SuccessCallback callback) {
            successCallback = callback;
            return this;
        }

        //开始获取图片
        public void start() {
            //判断来源类型是否为空
            if (sourceType != null) {
                //判断来源类型是否为全部
                if (sourceType == ALL) {
                    //弹出选择框,选拍照还是相册
                    new AlertDialog.Builder(contents)
                            .setItems(new String[]{contents.getString(R.string.Take_a_photo), contents.getString(R.string.Album)}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i) {
                                        case 0:
                                            sourceType = PHOTO;
                                            break;
                                        case 1:
                                            sourceType = ALBUM;
                                            break;
                                    }
                                    startFragment();
                                }
                            }).show();
                } else {
                    startFragment();
                }
            }
        }

        private void startFragment() {
            imageFragment = new ImageFragment(sourceType, new ImageFragment.OnImageReturnCallback() {
                @Override
                public void success(String... paths) {
                    successCallback.success(paths);
                    removeFragment();
                }
            });
            //获取FragmentManager
            FragmentManager fragmentManager = contents.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //将fragment加入到activity中
            fragmentTransaction.add(imageFragment, "imageFragment");
            //提交
            fragmentTransaction.commit();
        }

        //移除请求权限的fragment
        private void removeFragment() {
            FragmentManager fragmentManager = contents.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //将fragment从activity中移除
            fragmentTransaction.remove(imageFragment);
            fragmentTransaction.commit();
        }
    }

    //获取成功的回调
    public interface SuccessCallback {
        abstract void success(String... path);
    }
}
