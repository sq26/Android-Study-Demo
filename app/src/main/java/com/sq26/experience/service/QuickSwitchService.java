package com.sq26.experience.service;

import android.os.Build;
import android.service.quicksettings.TileService;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class QuickSwitchService extends TileService {
    @Override
    public void onTileAdded() {
        super.onTileAdded();
        // 当 tile 被添加时的回调
        getQsTile().setContentDescription ("添加");
        getQsTile().updateTile();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        // 当 tile 被移除时的回调
        getQsTile().setContentDescription ("移除");
        getQsTile().updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        // 当 tile 可见时的回调
        getQsTile().setContentDescription ("可见");
        getQsTile().updateTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        // 当 tile 不可见加时的回调
        getQsTile().setContentDescription ("隐藏");
        getQsTile().updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        // 当 tile 点击时的回调
        getQsTile().setContentDescription ("点击");
        getQsTile().updateTile();
    }
}
