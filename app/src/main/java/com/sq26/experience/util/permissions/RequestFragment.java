package com.sq26.experience.util.permissions;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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

    private RequestPermissionsCallback requestPermissionsCallback;
    private String[] requestPermissions;

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
        Log.e("1", "Fragment已经可以运行");

        this.requestPermissions(requestPermissions, 26);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 26) {
            List<String> successList = new ArrayList<>();
            List<String> failureList = new ArrayList<>();
            List<String> noPromptList = new ArrayList<>();

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    //获取权限成功
                    successList.add(permissions[i]);
                } else {
                    //获取权限失败
                    //shouldShowRequestPermissionRationale判断是否被拒绝弹框,true是会,false不会
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[i])) {
                        //会弹框
                        failureList.add(permissions[i]);
                    } else {
                        //不会弹框
                        noPromptList.add(permissions[i]);
                    }
                }
            }

            if (successList.size() == permissions.length) {
                requestPermissionsCallback.success();
            } else {
                requestPermissionsCallback.failure(successList.toArray(new String[0]), failureList.toArray(new String[0]), noPromptList.toArray(new String[0]));
            }
        }
    }

    interface RequestPermissionsCallback {
        abstract void success();

        abstract void failure(String[] successArray, String[] failureArray, String[] noPromptArray);
    }
}
