package com.sq26.experience.util.media;

import android.content.Context;
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
    public static Builder initialize(Context context) {
        return new Builder((AppCompatActivity) context);
    }

    //构造器类
    public static class Builder {
        //上下文
        private AppCompatActivity context;
        //图片来源
        private Integer sourceType;
        //获取成功的回调
        private SuccessCallback successCallback;
        //获取拍照的fragment
        private ImageFragment imageFragment;
        //最大图片数量,默认是0,表示不限制
        private int maxCount = 0;
        //是否压缩拍照的图片
        private boolean isCompression = false;
        //压缩后的图片和拍照的图片是否是长期保留(true保存在app内部,默认保存在app缓存中)
        private boolean isLastingSave = false;
        //构建
        public Builder(AppCompatActivity context) {
            this.context = context;
        }

        //设置图片来源(从相册还是拍照)
        public Builder setImageSource(Integer sourceType) {
            this.sourceType = sourceType;
            return this;
        }

        //设置拍照是否压缩(默认不压缩)
        public Builder isCompression(boolean isCompression) {
            this.isCompression = isCompression;
            return this;
        }

        //设置图片来源(从相册还是拍照)
        public Builder setMaxCount(int maxCount) {
            this.maxCount = maxCount;
            return this;
        }

        //设置压缩后的图片和拍照的图片是否是长期保留(true保存在app内部,默认保存在app缓存中)
        public Builder isLastingSave(boolean isLastingSave) {
            this.isLastingSave = isLastingSave;
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
                    new AlertDialog.Builder(context)
                            .setItems(new String[]{context.getString(R.string.Take_a_photo), context.getString(R.string.Album)}, new DialogInterface.OnClickListener() {
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
            JPermissions.init(context)
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
            imageFragment = new ImageFragment(sourceType, maxCount, isCompression,isLastingSave, new ImageFragment.OnImageReturnCallback() {
                @Override
                public void success(String... paths) {
                    //成功获取到图片后的回调
                    successCallback.success(paths);
                    //移除创建的fragment
                    removeFragment();
                }

                @Override
                public void canceled() {
                    //移除创建的fragment
                    removeFragment();
                }
            });
            //获取FragmentManager
            FragmentManager fragmentManager = context.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //将fragment加入到activity中
            fragmentTransaction.add(imageFragment, "imageFragment");
            //提交
            fragmentTransaction.commit();
        }

        //移除请求权限的fragment
        private void removeFragment() {
            FragmentManager fragmentManager = context.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //将fragment从activity中移除
            fragmentTransaction.remove(imageFragment);
            //提交
            fragmentTransaction.commit();
        }
    }

    //获取成功的回调
    public interface SuccessCallback {
        void success(String... path);
    }
}
