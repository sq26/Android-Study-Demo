package com.sq26.androidstudydemo.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.sq26.androidstudydemo.R;
import com.sq26.androidstudydemo.util.StringToAscii;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothInfoDemo2Activity extends AppCompatActivity {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.mac)
    TextView mac;
    @BindView(R.id.uuid)
    TextView uuid;

    BluetoothDevice bluetoothDevice;
    Context mContext;
    BluetoothAdapter mBluetoothAdapter;
    @BindView(R.id.textView8)
    TextView textView8;
    @BindView(R.id.editText2)
    EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_info);
        ButterKnife.bind(this);
        mContext = this;
        uuid.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView8.setMovementMethod(ScrollingMovementMethod.getInstance());
        init();
    }

    private void init() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevice = mBluetoothAdapter.getRemoteDevice(getIntent().getStringExtra("mac"));
        name.setText(bluetoothDevice.getName());
        mac.setText(bluetoothDevice.getAddress());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuid", bluetoothDevice.getUuids());
        uuid.setText(jsonObject.toJSONString());
    }

    private BluetoothGatt mBluetoothGatt;

    @OnClick({R.id.button3, R.id.button7, R.id.cllog})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button3:
                connect();
                break;
            case R.id.button7:
                new OutThread().start();
                break;
            case R.id.cllog:
                buffer = new StringBuffer();
                updateLog("");
                break;
        }
    }

    private void connect() {
        new ConnectThread(bluetoothDevice).start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                updateLog("开始获取服务");
                // MY_UUID is the app's UUID string, also used by the server code
//                UUID MY_UUID = UUID.fromString("7a541692-7150-4321-bb34-653dfd9d65a3");
//                UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                updateLog("获取服务失败");
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception

                updateLog("开始连接服务");
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out

                updateLog("连接服务失败");
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    updateLog("关闭服务失败1");
                }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                updateLog("关闭服务失败2");
            }
        }
    }

    BluetoothSocket bluetoothSocketOne;

    private void manageConnectedSocket(BluetoothSocket socket) {
        bluetoothSocketOne = socket;
        updateLog("成功与服务器连接");
        new InputThread().start();
    }

    private StringBuffer buffer = new StringBuffer();

    private void updateLog(String s) {
        buffer.append(s);
        buffer.append("\n");
        textView8.post(new Runnable() {
            @Override
            public void run() {
                textView8.setText(buffer.toString());
            }
        });
    }


    private class InputThread extends Thread {
        boolean b = true;

        public InputThread() {

        }

        public void run() {

            while (b) {
                InputStream inputStream = null;
                try {
                    inputStream = bluetoothSocketOne.getInputStream();
                    if (inputStream != null) {
                        if (inputStream.available() > 0) {
                            byte[] bytes = new byte[inputStream.available()];
                            inputStream.read(bytes);
                            String str = new String(bytes, "UTF-8");
                            if (!str.isEmpty()) {
//                                updateLog("从服务端接收到的消息str:" + str);
                                String hexString = StringToAscii.bytesToString10(bytes);
                                updateLog("从服务端接收到的消息str:" + hexString);
//                                if (hexString.length() == 12 && hexString.substring(0, 2).equals("aa") && hexString.substring(10, 12).equals("55")) {
//                                    updateLog("从服务端接收到的消息str:" + hexString);
//                                    Log.i("从服务端接收到的消息str", hexString);
//                                    String s1 = hexString.substring(2, 4);
//                                    String s2 = hexString.substring(4, 6);
//                                    String s3 = hexString.substring(6, 8);
//                                    String s4 = hexString.substring(8, 10);
//                                    Log.i("第一字节", StringToAscii.HexToInt10(s1) + "");
//                                    Log.i("第二字节", StringToAscii.HexToInt10(s2) + "");
//                                    Log.i("第三字节", StringToAscii.HexToInt10(s3) + "");
//                                    Log.i("第四字节", StringToAscii.HexToInt10(s4) + "");
//                                    updateLog("第一字节:" + StringToAscii.HexToInt10(s1));
//                                    updateLog("第二字节:" + StringToAscii.HexToInt10(s2));
//                                    updateLog("第三字节:" + StringToAscii.HexToInt10(s3));
//                                    updateLog("第四字节:" + StringToAscii.HexToInt10(s4));
//
//                                }
                            }
                        }

                    }
                } catch (IOException e) {
                    updateLog("接收消息失败");
                    updateLog("服务可能已经断开");
                    updateLog("请重新连接服务");
                    b = false;
                    e.printStackTrace();
                } finally {
//                    if (inputStream != null) {
//                        try {
//                            inputStream.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
                }
            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            b = false;
        }
    }

    private class OutThread extends Thread {
        public OutThread() {

        }

        public void run() {
            if (bluetoothSocketOne.isConnected()) {
                OutputStream outStream = null;
                try {
                    updateLog("发送消息:" + editText2.getText().toString());
                    outStream = bluetoothSocketOne.getOutputStream();
                    outStream.write(editText2.getText().toString().getBytes());
//                    outStream.write(StringToAscii.stringToAscii16byte(editText2.getText().toString()));
                } catch (IOException e) {
                    updateLog("发送消息失败");
                    updateLog("服务可能已经断开");
                    e.printStackTrace();
                } finally {
                    if (outStream != null) {
                        try {
                            outStream.flush();
                            outStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                updateLog("Socket服务已经断开");
            }

        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {

        }
    }

}
