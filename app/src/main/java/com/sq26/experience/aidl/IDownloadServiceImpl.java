package com.sq26.experience.aidl;


import android.os.RemoteException;

import java.util.Map;

public class IDownloadServiceImpl extends IDownloadServiceInterface.Stub {


    @Override
    public Map getDownloadInfo(String url) throws RemoteException {
        return null;
    }

    @Override
    public void registerCallback(String url, IDownloadServiceCallbackInterface callback) throws RemoteException {

    }

    @Override
    public void unregisterCallback(String url) throws RemoteException {

    }
}
