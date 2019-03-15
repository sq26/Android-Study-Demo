// IMyAidl.aidl
package com.sq26.androidstudydemo;

// Declare any non-default types here with import statements
import com.sq26.androidstudydemo.aidl.AllBroadcasting;

interface IMyAidl {
    void inValue(in AllBroadcasting allBroadcasting);

    List<Person> getValueList();

}
