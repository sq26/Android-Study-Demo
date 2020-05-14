package com.sq26.experience.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.sq26.experience.aidl.IAidlInterface;
import com.sq26.experience.aidl.ICallbackAidlInterface;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class AIDLService extends Service {

    private Map<String, ICallbackAidlInterface> map = new ConcurrentHashMap<>();

    private IAidlInterface.Stub iAidlInterface = new IAidlInterface.Stub() {
        @Override
        public void pushText(String text) throws RemoteException {
            for (String key : map.keySet()) {
                Objects.requireNonNull(map.get(key)).pull(text);
            }
        }

        @Override
        public void registerCallback(String packageName, ICallbackAidlInterface callback) throws RemoteException {
            map.put(packageName, callback);
        }

        @Override
        public void unregisterCallback(String packageName) throws RemoteException {
            map.remove(packageName);
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return iAidlInterface;
    }
}
