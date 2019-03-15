package com.sq26.androidstudydemo.service;

import android.app.Person;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.sq26.androidstudydemo.IMyAidl;
import com.sq26.androidstudydemo.aidl.AllBroadcasting;

import java.util.List;

public class DemoService extends Service {

    private IBinder iBinder = new IMyAidl.Stub() {
        @Override
        public void inValue(AllBroadcasting allBroadcasting) throws RemoteException {
            Log.i("服务端",allBroadcasting.getValue());
            System.out.println("服务端"+allBroadcasting.getValue());
        }

        @Override
        public List<Person> getValueList() {
            return null;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("服务端","创建完成");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
