// IDownloadServiceCallbackInterface.aidl
package com.sq26.experience.aidl;

// Declare any non-default types here with import statements

interface IDownloadServiceCallbackInterface {
    void onProgress(in Map downloadMap);
}
