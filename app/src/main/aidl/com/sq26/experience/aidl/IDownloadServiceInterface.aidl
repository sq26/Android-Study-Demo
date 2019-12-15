
package com.sq26.experience.aidl;

import com.sq26.experience.aidl.IDownloadServiceCallbackInterface;

interface IDownloadServiceInterface {

    Map getDownloadInfo(String url);

    void registerCallback(String url,IDownloadServiceCallbackInterface callback);

    void unregisterCallback(String url);

}
