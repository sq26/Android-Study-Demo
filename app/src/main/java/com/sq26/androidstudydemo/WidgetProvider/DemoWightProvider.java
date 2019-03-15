package com.sq26.androidstudydemo.WidgetProvider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.RemoteViews;

import com.alibaba.fastjson.JSONArray;
import com.sq26.androidstudydemo.IMyAidl;
import com.sq26.androidstudydemo.R;
import com.sq26.androidstudydemo.aidl.AllBroadcasting;
import com.sq26.androidstudydemo.service.DemoService;

public class DemoWightProvider extends AppWidgetProvider {
    //每当组件从屏幕上移除
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.i("组件生命周期", "每当组件从屏幕上移除");
    }

    //当最后一个该Widget删除是调用该方法，注意是最后一个
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.i("组件生命周期", "当最后一个该Widget删除是调用该方法");
//        context.getApplicationContext().unbindService(connection);
    }

    // 当该Widget第一次添加到桌面是调用该方法，可添加多次但只第一次调用
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.i("组件生命周期", "当该Widget第一次添加到桌面是调用该方法");
//        context.getApplicationContext().bindService(new Intent(context.getApplicationContext(), DemoService.class), connection, Context.BIND_AUTO_CREATE);
    }

    private JSONArray jsonArray = new JSONArray();

    // 每接收一次广播消息就调用一次
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        Log.i("组件生命周期", "每接收一次广播消息就调用一次:" + intent.getAction());
        jsonArray.add(intent.getAction());
        remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_demo);
        if (widgetIds != null) {
            remoteView.setTextViewText(R.id.textView, jsonArray.toJSONString());
            updateAllAppWidgets(AppWidgetManager.getInstance(context), widgetIds);
        }
    }

    private IMyAidl iMyAidl;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iMyAidl = IMyAidl.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iMyAidl = null;
        }
    };

    //每次更新都调用一次该方法
    RemoteViews remoteView;
    int[] widgetIds;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // TODO Auto-generated method stub
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        widgetIds = appWidgetIds;
        Log.i("组件生命周期", "每次更新都调用一次该方法");
    }

    private void updateAllAppWidgets(AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        appWidgetManager.updateAppWidget(appWidgetIds, remoteView);
    }
}
