package com.sq26.androidstudydemo.activity;

import android.nfc.FormatException;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sq26.androidstudydemo.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class NFCActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        init();
    }

    private void init() {
        try {
            InputStream inputStream = getResources().getAssets().open("Link.bin");
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);


            Log.i("bytesToHexString",bytesToHexString(bytes));
            Log.i("hexStr2Str",hexStr2Str(bytesToHexString(bytes)));
            Log.i("utf-16", new String(bytes, "UTF-16"));
//            for (byte b : bytes) {
//                Log.i("s1", b + "");
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static String hexStr2Str(String hexStr) {

        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        try {
            return new String(bytes,"UTF-16");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new String("");
    }
}
