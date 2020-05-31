package com.sq26.experience.util.media;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.sq26.experience.BuildConfig;
import com.sq26.experience.R;
import com.sq26.experience.ui.dialog.ProgressDialog;
import com.sq26.experience.ui.util.SelectImageActivity;
import com.sq26.experience.util.ImageCompressionUtil;

import java.io.File;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Thread.sleep;

public class ImageFragment extends Fragment {
    //记录图片数据的来源
    private Integer sourceType;
    //记录最大图片数量
    private int maxCount;
    //获取数据结束后的回调类
    private OnImageReturnCallback onImageReturnCallback;

    //构造初始化
    ImageFragment(Integer sourceType, int maxCount, OnImageReturnCallback onImageReturnCallback) {
        this.sourceType = sourceType;
        this.maxCount = maxCount;
        this.onImageReturnCallback = onImageReturnCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private File cameraSavePath;//拍照照片路径

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //判断来源类型
        if (sourceType == JImage.PHOTO) {//拍照
            //获取系统时间戳
            long timeStamp = System.currentTimeMillis();
            //创建拍照保存的图片路径,用时间戳做文件名
            cameraSavePath = new File(Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_PICTURES), timeStamp + ".png");
            //创建intent,跳转相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //创建照片位置uri
            Uri uri;
            //判断Android版本大于等于24(也就是Android7.0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //加入临时访问声明
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //将图片路劲转成Uri
                uri = FileProvider.getUriForFile(Objects.requireNonNull(getActivity()), BuildConfig.APPLICATION_ID + ".FileProvider", cameraSavePath);
            } else {
                //将图片路劲转成Uri
                uri = Uri.fromFile(cameraSavePath);
            }
            //设置保存后的输出url
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            //启动跳转到相机
            startActivityForResult(intent, JImage.PHOTO);
        } else if (sourceType == JImage.ALBUM) {//相册
            //创建intent,跳转到自己写的相册(使用系统相册无法完美获取图片路径)
            Intent intent = new Intent(getActivity(), SelectImageActivity.class);
            //设置最大选择数量
            intent.putExtra("maxCount", maxCount);
            //启动跳转到相册
            startActivityForResult(intent, JImage.ALBUM);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //判断是不是成功的返回
        if (resultCode == Activity.RESULT_OK) {
            //判断是从哪里返回的数据
            switch (requestCode) {
                case JImage.PHOTO://相机
                    onImageReturnCallback.success(cameraSavePath.getAbsolutePath());
                    break;
                case JImage.ALBUM://相册
                    String[] paths = Objects.requireNonNull(data).getStringArrayExtra("paths");
                    //判断有没有选择
                    if (paths.length > 0) {
//                        //判断是否要原图
//                        if (data.getBooleanExtra("isOriginal", false)) {
                            //要原图直接回调
                            onImageReturnCallback.success(paths);
//                        } else {
//                            //不要原图,进行压缩
//                            //创建一个不可被用户关闭的等待弹出框
//                            ProgressDialog progressDialog = new ProgressDialog(Objects.requireNonNull(getActivity()));
//                            //显示等待弹出框
//                            progressDialog.show();
//                            //创建一个rxjava,进行异步压缩图片
//                            Observable.create(new ObservableOnSubscribe<Integer>() {
//                                @Override
//                                public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                                    //遍历已选择的图片
//                                    for (int i = 0; i < paths.length; i++) {
//                                        //调用图片压缩方法将压缩过的图片的地址进行替换
//                                        paths[i] = ImageCompressionUtil.startCompression(getActivity(), paths[i]);
//                                        //每成功压缩一次,调用一次提示方法
//                                        emitter.onNext(i);
//                                    }
//                                    //压缩遍历完调用完成方法
//                                    emitter.onComplete();
//                                }
//                            }).observeOn(AndroidSchedulers.mainThread())
//                                    .subscribeOn(Schedulers.io())
//                                    .subscribe(new Observer<Integer>() {
//                                        @Override
//                                        public void onSubscribe(Disposable d) {
//                                            //异步开始的方法
//                                        }
//
//                                        @Override
//                                        public void onNext(Integer i) {
//                                            //在这里进行提示刷新,每成功压缩一次,刷新一下提示
//                                            progressDialog.setMessage(Objects.requireNonNull(getContext()).getString(R.string.Compressing_2d, (i + 1), paths.length));
//                                        }
//
//                                        @Override
//                                        public void onError(Throwable e) {
//                                            //异步线程出错的方法
//                                        }
//
//                                        @Override
//                                        public void onComplete() {
//                                            //完成,关闭等待弹出框
//                                            progressDialog.dismiss();
//                                            //进行图片获取成功的回调
//                                            onImageReturnCallback.success(paths);
//                                        }
//                                    });
//                        }
                    } else
                        //进行图片获取取消的回调
                        onImageReturnCallback.canceled();
                    break;

            }
        } else {
            //进行图片获取 取消的回调
            onImageReturnCallback.canceled();
        }
    }

    //获取图片的回调类
    interface OnImageReturnCallback {
        //成功的回调
        void success(String... paths);

        //取消的回调
        void canceled();
    }
}
