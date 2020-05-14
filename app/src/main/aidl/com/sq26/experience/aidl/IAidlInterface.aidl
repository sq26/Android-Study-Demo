// IAidlInterface.aidl
package com.sq26.experience.aidl;

import com.sq26.experience.aidl.ICallbackAidlInterface;
// Declare any non-default types here with import statements

interface IAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void pushText(String text);

    void registerCallback(String packageName,ICallbackAidlInterface callback);

    void unregisterCallback(String packageName);
}
