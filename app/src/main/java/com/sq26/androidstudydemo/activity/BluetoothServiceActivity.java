package com.sq26.androidstudydemo.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

public class BluetoothServiceActivity extends AppCompatActivity {

    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.editText2)
    EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_service);
        ButterKnife.bind(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        textView3.setMovementMethod(ScrollingMovementMethod.getInstance());
    }


    private void startBlueService() {
        new AcceptThread().start();
    }

    BluetoothAdapter mBluetoothAdapter;

    @OnClick({R.id.button6, R.id.button7})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button6:
                startBlueService();
                break;
            case R.id.button7:
                new OutThread().start();
                break;
        }
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code

                updateLog("开始启动服务");
                UUID MY_UUID = UUID.fromString("7a541692-7150-4321-bb34-653dfd9d65a3");
//                UUID MY_UUID = UUID.fromString("00001124-0000-1000-8000-00805f9b34fb");
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("demo", MY_UUID);
            } catch (IOException e) {
                updateLog("启动服务失败");
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;

            updateLog("等待客户端的连接");
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    updateLog("获取客户端连接失败");
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        updateLog("关闭服务失败");
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                updateLog("关闭服务失败2");
            }
        }
    }

    BluetoothSocket bluetoothSocketOne;

    private void manageConnectedSocket(BluetoothSocket bluetoothSocket) {
        bluetoothSocketOne = bluetoothSocket;
        updateLog("获取到来之客户端的连接");
        new InputThread().start();
    }

    private StringBuffer buffer = new StringBuffer();

    private void updateLog(String s) {
        buffer.append(s);
        buffer.append("\n");
        textView3.post(new Runnable() {
            @Override
            public void run() {
                textView3.setText(buffer.toString());
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
                            String msg = StringToAscii.asciiToString(str);
                            updateLog("从客户端接收到的消息:" + msg);
                            Log.i("服务端", "从客户端接收到的消息" + msg);
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
                    outStream.write(StringToHex.hexStringToByteArray(StringToHex.stringToHexString(editText2.getText().toString())));
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
