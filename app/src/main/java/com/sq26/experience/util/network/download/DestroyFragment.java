package com.sq26.experience.util.network.download;

import androidx.fragment.app.Fragment;

public class DestroyFragment extends Fragment {
    private DestroyCallback destroyCallback;

    public  DestroyFragment (DestroyCallback destroyCallback){
        this.destroyCallback = destroyCallback;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyCallback.destroy(this);
    }

    //页面即将回收的回调类
    interface DestroyCallback {
        //即将回收的回调
        abstract void destroy(DestroyFragment destroyFragment);
    }
}
