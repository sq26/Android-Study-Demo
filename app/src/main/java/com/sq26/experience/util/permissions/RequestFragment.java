package com.sq26.experience.util.permissions;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class RequestFragment extends Fragment {
    //权限请求的回调
    private RequestPermissionsCallback requestPermissionsCallback;
    //要请求的所有类型
    private String[] requestPermissions;
    //构造初始化
    public RequestFragment(String[] requestPermissions, RequestPermissionsCallback requestPermissionsCallback) {
        this.requestPermissions = requestPermissions;
        this.requestPermissionsCallback = requestPermissionsCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //开始请求权限
        this.requestPermissions(requestPermissions, 26);

    }
    //权限申请后的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //requestCode:请求状态码,permissions:请求的所有权限,grantResults:请求的所有权限的权限获取状态
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //判断是否是工具自己发出的权限申请的回调
        if (requestCode == 26) {
            //成功的权限列表
            List<String> successList = new ArrayList<>();
            //失败的权限列表
            List<String> failureList = new ArrayList<>();
            //不仅失败并且无法再次申请的权限列表
            List<String> noPromptList = new ArrayList<>();
            //循环遍历权限状态
            for (int i = 0; i < permissions.length; i++) {
                //判断是否是成功状态
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    //获取权限成功
                    successList.add(permissions[i]);
                } else {
                    //获取权限失败
                    //shouldShowRequestPermissionRationale判断是否被拒绝弹框,true是会弹框,false不会弹框
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[i])) {
                        //会弹框
                        failureList.add(permissions[i]);
                    } else {
                        //不会弹框
                        noPromptList.add(permissions[i]);
                    }
                }
            }
            //判断成功的权限是否==申请的权限
            if (successList.size() == permissions.length) {
                //全部申请成功
                requestPermissionsCallback.success();
            } else {
                //不全部成功
                requestPermissionsCallback.failure(successList.toArray(new String[0]), failureList.toArray(new String[0]), noPromptList.toArray(new String[0]));
            }
        }
    }
    //申请结果回调类
    interface RequestPermissionsCallback {
        //成功的回调
        abstract void success();
        //失败的回调
        abstract void failure(String[] successArray, String[] failureArray, String[] noPromptArray);
    }
}
