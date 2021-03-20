// IAidlInterface.aidl
package com.sq26.experience.aidl;

import com.sq26.experience.aidl.ICallbackAidlInterface;
// Declare any non-default types here with import statements

interface IAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     //发送信息
    void pushText(String text);
    //注册接收信息的回调
    void registerCallback(String name,ICallbackAidlInterface callback);
    //注销接收信息的回调
    void unregisterCallback(String name);
}
