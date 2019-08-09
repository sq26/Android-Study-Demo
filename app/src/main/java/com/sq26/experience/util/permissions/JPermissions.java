package com.sq26.experience.util.permissions;


import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class JPermissions {
    public static Builder init(AppCompatActivity c) {
        Builder builder = new Builder(c);
        return builder;
    }

    public static class Builder {
        private AppCompatActivity contents;
        private JPermissions.SuccessCallback successCallback;
        private JPermissions.FailureCallback failureCallback;

        private RequestFragment requestFragment;
        private String[] requestPermissions;

        public Builder(AppCompatActivity contents) {
            this.contents = contents;
        }

        public Builder permissions(String... requestPermissions) {
            this.requestPermissions = requestPermissions;
            return this;
        }

        public Builder success(JPermissions.SuccessCallback callback) {
            successCallback = callback;
            return this;
        }

        public Builder failure(JPermissions.FailureCallback callback) {
            failureCallback = callback;
            return this;
        }

        public Builder getBuilder() {
            return this;
        }

        public void start() {
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
                        noPromptList.add(s);
                    }
                }


            if (pendingApplicationList.size() == 0 && noPromptList.size() == 0) {
                successCallback.success();
                return;
            } else {
                if (noPromptList.size() == requestPermissions.length) {
                    failureCallback.failure(new String[]{}, new String[]{}, noPromptList.toArray(new String[0]));
                    return;
                }
            }

            requestFragment = new RequestFragment(pendingApplicationList.toArray(new String[0]), new RequestFragment.RequestPermissionsCallback() {
                @Override
                public void success() {
                    successCallback.success();
                    removeFragment();
                }

                @Override
                public void failure(String[] successArray, String[] failureArray, String[] noPromptArray) {
                    failureCallback.failure(successArray, failureArray, noPromptArray);
                    removeFragment();
                }
            });
            FragmentManager fragmentManager = contents.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(requestFragment, "1");
            fragmentTransaction.commit();
        }

        private void removeFragment() {
            FragmentManager fragmentManager = contents.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(requestFragment);
            fragmentTransaction.commit();
        }

    }

    public interface SuccessCallback {
        abstract void success();
    }


    public interface FailureCallback {
        abstract void failure(String[] successArray, String[] failureArray, String[] noPromptArray);
    }

}
