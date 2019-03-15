package com.sq26.androidstudydemo.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class AllBroadcasting implements Parcelable {
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;

    public AllBroadcasting(String value) {
        this.value = value;
    }

    protected AllBroadcasting(Parcel in) {
        value = in.readString();
    }



    public static final Creator<AllBroadcasting> CREATOR = new Creator<AllBroadcasting>() {
        @Override
        public AllBroadcasting createFromParcel(Parcel in) {
            return new AllBroadcasting(in);
        }

        @Override
        public AllBroadcasting[] newArray(int size) {
            return new AllBroadcasting[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
