package com.sq26.experience.util.media;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sq26.experience.R;
import com.sq26.experience.util.permissions.JPermissions;
import com.sq26.experience.util.permissions.PermissionUtil;


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
                                            //更改来源为拍照
                                            sourceType = PHOTO;
                                            break;
                                        case 1:
                                            //更改来源为相册
                                            sourceType = ALBUM;
                                            break;
                                    }
                                    //判断有没有权限没有就申请权限
                                    requestPermissions();
                                }
                            }).show();
                } else {
                    //判断有没有权限没有就申请权限
                    requestPermissions();
                }
            }
        }

        //申请权限
        private void requestPermissions() {
            String[] permissions;
            //判断是不是拍照
            if (sourceType == PHOTO)
                //选择拍照申请拍照的权限
                permissions = PermissionUtil.Group.CAMERA;
            else
                //否则就是相册的权限,也就是储存权限
                permissions = PermissionUtil.Group.STORAGE;
            //开始调用权限申请
            JPermissions.init(contents)
                    .permissions(permissions)
                    .success(new JPermissions.SuccessCallback() {
                        @Override
                        public void success() {
                            //成功后去执行fragment,进去内容获取操作
                            startFragment();
                        }
                    })
                    .failure(new JPermissions.FailureCallback() {
                        @Override
                        public void failure(String[] successArray, String[] failureArray, String[] noPromptArray) {
                            //失败后弹出提示(暂时没想好怎么提示)
                        }
                    })
                    .start();
        }

        private void startFragment() {
            //创建可选择图片的fragment,并初始化回调
            imageFragment = new ImageFragment(sourceType, new ImageFragment.OnImageReturnCallback() {
                @Override
                public void success(String... paths) {
                    //成功获取到图片后的回调
                    successCallback.success(paths);
                    //移除创建的fragment
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
