package com.sq26.experience.util.media;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ImageFragment extends Fragment {
    private Integer sourceType;
    private OnImageReturnCallback onImageReturnCallback;

    //构造初始化
    public ImageFragment(Integer sourceType, OnImageReturnCallback onImageReturnCallback) {
        this.sourceType = sourceType;
        this.onImageReturnCallback = onImageReturnCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (sourceType == JImage.PHOTO) {

        } else if (sourceType == JImage.ALBUM) {

        }
    }

    //申请结果回调类
    interface OnImageReturnCallback {
        //成功的回调
        abstract void success(String... paths);
    }
}
