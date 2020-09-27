package com.sq26.experience.util.media;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sq26.experience.BuildConfig;
import com.sq26.experience.R;
import com.sq26.experience.ui.dialog.ProgressDialog;
import com.sq26.experience.ui.util.SelectImageActivity;
import com.sq26.experience.util.FileType;
import com.sq26.experience.util.ImageCompression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    //设置拍照是否压缩
    private boolean isCompression;
    //压缩后的图片和拍照的图片是否是长期保留(true保存在app内部,默认保存在app缓存中)
    private boolean isLastingSave;
    //获取数据结束后的回调类
    private OnImageReturnCallback onImageReturnCallback;

    //构造初始化
    ImageFragment(Integer sourceType, int maxCount, boolean isCompression, boolean isLastingSave, OnImageReturnCallback onImageReturnCallback) {
        this.sourceType = sourceType;
        this.maxCount = maxCount;
        this.isCompression = isCompression;
        this.isLastingSave = isLastingSave;
        this.onImageReturnCallback = onImageReturnCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private String cameraTemporarySavePath;//拍照照片临时路径

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //判断来源类型
        if (sourceType == JImage.PHOTO) {//拍照
            //获取系统时间戳
            long timeStamp = System.currentTimeMillis();
            //创建拍照保存的图片文件,用时间戳做文件名,这里不加后缀(因为无法设置相机的拍摄格式)
            File cameraSaveFile = new File(isLastingSave ?
                    Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_PICTURES) :
                    Objects.requireNonNull(getActivity()).getExternalCacheDir(), timeStamp + "");
            //获取文件的临时路径
            cameraTemporarySavePath = cameraSaveFile.getAbsolutePath();
            //创建intent,跳转相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //创建照片位置uri
            Uri uri;
            //判断Android版本大于等于24(也就是Android7.0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //加入临时访问声明
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //将图片路劲转成Uri
                uri = FileProvider.getUriForFile(Objects.requireNonNull(getActivity()), BuildConfig.APPLICATION_ID + ".FileProvider", cameraSaveFile);
            } else {
                //将图片路劲转成Uri
                uri = Uri.fromFile(cameraSaveFile);
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
                    //获取文件类型
                    String fileType = FileType.getFileType(cameraTemporarySavePath);
                    //创建新的文件路径,设置文件类型
                    File renameFile = new File(cameraTemporarySavePath + "." + fileType);
                    //对临时文件进行重命名,修改文件类型
                    new File(cameraTemporarySavePath)
                            .renameTo(renameFile);
                    //判断拍照图片是否压缩
                    if (isCompression) {
                        //进行压缩
                        //创建一个不可被用户关闭的等待弹出框,并显示
                        ProgressDialog progressDialog = new ProgressDialog(Objects.requireNonNull(getActivity())).show();
                        //创建MutableLiveData做异步处理
                        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
                        //设置观察者,监听数据变化
                        mutableLiveData.observe(this, s -> {
                            //主线操作
                            //完成,关闭等待弹出框
                            progressDialog.dismiss();
                            //进行图片获取成功的回调
                            onImageReturnCallback.success(s);
                        });
                        //起一个子线程进行图片压缩
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //获取文件的路径
                                String newFile = new ImageCompression(getActivity())
                                        .uri(renameFile.getAbsolutePath())
                                        .startCompressionToString();
                                //判断新路径和旧路径是否相同
                                if (!newFile.equals(renameFile.getAbsolutePath())) {
                                    //不同,说明压缩过,删除原文件
                                    renameFile.delete();
                                }
                                //压缩完成直接刷新数据
                                mutableLiveData.postValue(newFile);
                            }
                        }).start();
                    } else {
                        //不压缩直接返回文件路径
                        onImageReturnCallback.success(renameFile.getAbsolutePath());
                    }
                    break;
                case JImage.ALBUM://相册
                    String[] paths = Objects.requireNonNull(data).getStringArrayExtra("paths");
                    //判断有没有选择
                    if (Objects.requireNonNull(paths).length > 0) {
//                        //判断是否要原图
                        if (data.getBooleanExtra("isOriginal", false)) {
                            //要原图直接回调
                            onImageReturnCallback.success(paths);
                        } else {
                            //不要原图,进行压缩
                            //创建一个不可被用户关闭的等待弹出框,并显示
                            ProgressDialog progressDialog = new ProgressDialog(Objects.requireNonNull(getActivity())).show();
                            //创建一个rxjava,进行异步压缩图片
                            Observable.create(new ObservableOnSubscribe<Integer>() {
                                @Override
                                public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                                    //遍历已选择的图片
                                    for (int i = 0; i < paths.length; i++) {
                                        //调用图片压缩方法将压缩过的图片的地址进行替换
                                        paths[i] = new ImageCompression(getActivity())
                                                //设置要压缩的链接
                                                .uri(paths[i])
                                                //设置储存方式
                                                .isLastingSave(isLastingSave)
                                                //执行压缩,并返回file路径
                                                .startCompressionToString();
                                        //每成功压缩一次,调用一次提示方法
                                        emitter.onNext(i);
                                    }
                                    //压缩遍历完调用完成方法
                                    emitter.onComplete();
                                }
                            }).observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Observer<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {
                                            //异步开始的方法
                                        }

                                        @Override
                                        public void onNext(Integer i) {
                                            //在这里进行提示刷新,每成功压缩一次,刷新一下提示
                                            progressDialog.setMessage(Objects.requireNonNull(getContext()).getString(R.string.Compressing_2d, (i + 1), paths.length));
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            //异步线程出错的方法
                                        }

                                        @Override
                                        public void onComplete() {
                                            //完成,关闭等待弹出框
                                            progressDialog.dismiss();
                                            //进行图片获取成功的回调
                                            onImageReturnCallback.success(paths);
                                        }
                                    });
                        }
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
