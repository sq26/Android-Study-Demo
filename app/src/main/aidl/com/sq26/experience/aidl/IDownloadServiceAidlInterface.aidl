// IDownloadServiceAidlInterface.aidl
package com.sq26.experience.aidl;

// Declare any non-default types here with import statements

interface IDownloadServiceAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    Map getDownloadInfo(String url);

    void setDownloadMap(in Map downloadMap);
}
