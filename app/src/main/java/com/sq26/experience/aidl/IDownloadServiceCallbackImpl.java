package com.sq26.experience.aidl;

import android.os.RemoteException;

import java.util.Map;

public class IDownloadServiceCallbackImpl extends IDownloadServiceCallbackInterface.Stub {
    @Override
    public void onProgress(Map downloadMap) throws RemoteException {

    }
}
