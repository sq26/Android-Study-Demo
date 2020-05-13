package com.sq26.experience.util.permissions;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class JPermissions {
    //初始化,创建构造器
    public static Builder init(Context context) {
        return new Builder((AppCompatActivity) context);
    }
    //构造器类
    public static class Builder {
        private AppCompatActivity contents;
        private SuccessCallback successCallback;
        private FailureCallback failureCallback;

        private RequestFragment requestFragment;
        private String[] requestPermissions;

        public Builder(AppCompatActivity contents) {
            this.contents = contents;
        }
        //设置要申请的权限
        public Builder permissions(String... requestPermissions) {
            this.requestPermissions = requestPermissions;
            return this;
        }
        //设置成功的回调
        public Builder success(SuccessCallback callback) {
            successCallback = callback;
            return this;
        }
        //设置失败的回调
        public Builder failure(FailureCallback callback) {
            failureCallback = callback;
            return this;
        }
        //返回构造器
        public Builder getBuilder() {
            return this;
        }
        //开始申请的方法
        public void start() {
            //判断要申请的权限列表不能为空
            if (requestPermissions == null || requestPermissions.length == 0) {
                successCallback.success();
                return;
            }

            List<String> pendingApplicationList = new ArrayList<>();//可以去申请的权限列表
            List<String> noPromptList = new ArrayList<>();//已被拒绝并且不在提示的权限列表
            for (String s : requestPermissions)
                //checkSelfPermission检擦是否有权限
                //PackageManager.PERMISSION_GRANTED有权限
                //PackageManager.PERMISSION_DENIED 无权限
                if (ActivityCompat.checkSelfPermission(contents, s) == PackageManager.PERMISSION_DENIED) {
                    //未被授权
                    //适配Android 23 6.0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //shouldShowRequestPermissionRationale判断是否被拒绝弹框,true是会,false不会
                        if (ActivityCompat.shouldShowRequestPermissionRationale(contents, s)) {
                            //不会弹框,需要解释
                            noPromptList.add(s);
                        } else {
                            //会弹框
                            pendingApplicationList.add(s);
                        }
                    } else {
                        //不会弹框,需要解释
                        noPromptList.add(s);
                    }
                }

            //判断是否所有要申请的权限都需要申请
            if (pendingApplicationList.size() == 0 && noPromptList.size() == 0) {
                //要申请的权限都已拥有权限,只接调用成功回调
                successCallback.success();
                return;
            } else {
                //当不在弹窗的权限数量和所有请求的权限一致时直接调用失败回调
                if (noPromptList.size() == requestPermissions.length) {
                    failureCallback.failure(new String[]{}, new String[]{}, noPromptList.toArray(new String[0]));
                    return;
                }
            }
            //创建请求权限的fragment,并传递要申请的权限列表和回调
            requestFragment = new RequestFragment(pendingApplicationList.toArray(new String[0]), new RequestFragment.RequestPermissionsCallback() {
                @Override
                public void success() {
                    //成功的回调
                    successCallback.success();
                    removeFragment();
                }

                @Override
                public void failure(String[] successArray, String[] failureArray, String[] noPromptArray) {
                    //失败的回调
                    failureCallback.failure(successArray, failureArray, noPromptArray);
                    removeFragment();
                }
            });
            //获取FragmentManager
            FragmentManager fragmentManager = contents.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //将fragment加入到activity中
            fragmentTransaction.add(requestFragment, "1");
            //提交
            fragmentTransaction.commit();
        }
        //移除请求权限的fragment
        private void removeFragment() {
            FragmentManager fragmentManager = contents.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //将fragment从activity中移除
            fragmentTransaction.remove(requestFragment);
            fragmentTransaction.commit();
        }

    }
    //申请成功的回调
    public interface SuccessCallback {
        abstract void success();
    }

    //申请失败的回调
    public interface FailureCallback {
        abstract void failure(String[] successArray, String[] failureArray, String[] noPromptArray);
    }

}
