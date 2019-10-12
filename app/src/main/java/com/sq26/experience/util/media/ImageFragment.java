package com.sq26.experience.util.media;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.sq26.experience.BuildConfig;
import com.sq26.experience.ui.util.SelectImageActivity;

import java.io.File;
import java.util.Objects;

public class ImageFragment extends Fragment {
    private Integer sourceType;
    private OnImageReturnCallback onImageReturnCallback;

    //构造初始化
    ImageFragment(Integer sourceType, OnImageReturnCallback onImageReturnCallback) {
        this.sourceType = sourceType;
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
        if (sourceType == JImage.PHOTO) {
            Uri uri;//照片uri
            long timeStamp = System.currentTimeMillis();
            cameraSavePath = new File(Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_PICTURES), timeStamp + ".png");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //判断Android版本大于等于24(也就是Android7.0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //加入临时访问声明
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //第二个参数为 包名.fileprovider
                uri = FileProvider.getUriForFile(Objects.requireNonNull(getActivity()), BuildConfig.APPLICATION_ID + ".FileProvider", cameraSavePath);
            } else {
                uri = Uri.fromFile(cameraSavePath);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, JImage.PHOTO);
        } else if (sourceType == JImage.ALBUM) {
            Intent intent = new Intent(getActivity(), SelectImageActivity.class);
            intent.putExtra("maxNum", 9);
            startActivityForResult(intent, JImage.ALBUM);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case JImage.PHOTO:
                    onImageReturnCallback.success(cameraSavePath.getAbsolutePath());
                    break;
                case JImage.ALBUM:
                    onImageReturnCallback.success(Objects.requireNonNull(data).getStringArrayExtra("paths"));
                    break;

            }
        }
    }

    //获取图片的回调类
    interface OnImageReturnCallback {
        //成功的回调
        void success(String... paths);
    }
}
