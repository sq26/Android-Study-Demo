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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.sq26.androidstudydemo.R;
import com.sq26.androidstudydemo.util.StringToAscii;
import com.sq26.androidstudydemo.util.StringToHex;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothInfoActivity extends AppCompatActivity {

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

    @OnClick({R.id.button3, R.id.button7})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button3:
                connect();
                break;
            case R.id.button7:
                new OutThread().start();
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
                UUID MY_UUID = UUID.fromString("7a541692-7150-4321-bb34-653dfd9d65a3");
//                UUID MY_UUID = UUID.fromString("00001124-0000-1000-8000-00805f9b34fb");
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
                try {
                    InputStream inputStream = bluetoothSocketOne.getInputStream();
                    if (inputStream != null) {
                        byte[] bytes = new byte[0];
                        bytes = new byte[inputStream.available()];
                        inputStream.read(bytes);
                        String str = new String(bytes);
                        if (!str.isEmpty()) {
                            String msg = StringToHex.hexStringToString(StringToHex.bytesToHexString(bytes));
                            updateLog("从服务端接收到的消息:" + msg);
                            Log.i("客户端", "从服务端接收到的消息" + msg);
                        }
                    }
                } catch (IOException e) {
                    updateLog("接收消息失败");
                    b = false;
                    e.printStackTrace();
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
        boolean b = true;

        public OutThread() {

        }

        public void run() {
            if (bluetoothSocketOne.isConnected()) {
                try {
                    OutputStream outStream = bluetoothSocketOne.getOutputStream();
                    outStream.write(StringToAscii.stringToAscii(editText2.getText().toString()).getBytes());
                } catch (IOException e) {
                    updateLog("发送消息失败");
                    e.printStackTrace();
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

}
