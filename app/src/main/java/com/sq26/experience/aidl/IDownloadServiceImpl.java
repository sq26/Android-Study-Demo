package com.sq26.experience.aidl;


import android.os.RemoteException;

import java.util.Map;

public class IDownloadServiceImpl extends IDownloadServiceAidlInterface.Stub {

    private Map downloadMap;

    @Override
    public Map getDownloadInfo(String url) throws RemoteException {
        return (Map) downloadMap.get(url);
    }

    @Override
    public void setDownloadMap(Map downloadMap) throws RemoteException {
        this.downloadMap = downloadMap;
    }
}
