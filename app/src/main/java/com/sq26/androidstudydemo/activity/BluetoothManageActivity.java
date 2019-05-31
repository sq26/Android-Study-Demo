package com.sq26.androidstudydemo.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.sq26.androidstudydemo.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BluetoothManageActivity extends AppCompatActivity {

    @BindView(R.id.BluetoothLayout)
    LinearLayout BluetoothLayout;
    BluetoothAdapter bluetoothAdapter;

    BluetoothReceiver bluetoothReceiver;
    private static final UUID BTMODULEUUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_manage);
        ButterKnife.bind(this);
        AndPermission.with(this)
                .runtime()
                .permission(Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION)
                .onGranted(permissions -> {
                    // Storage permission are allowed.
                    Log.i("onGranted", "获取授权");
                    init();
                })
                .onDenied(permissions -> {
                    // 判断用户是不是不再显示权限弹窗了，若不再显示的话进入权限设置页
                    if (AndPermission.hasAlwaysDeniedPermission(BluetoothManageActivity.this, permissions)) {
                        // 打开权限设置页
                        Log.i("onDenied", "不再显示权限弹窗了");
                    } else {
                        Log.i("onDenied", "用户拒绝权限");
                    }
                })
                .start();
    }

    private void init() {
        if (bluetoothAdapter != null) {
            return;
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothReceiver = new BluetoothReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(bluetoothReceiver, intentFilter);

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
    }

    @OnClick({R.id.button, R.id.button5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                if (bluetoothAdapter.isEnabled()) {
                    BluetoothLayout.removeAllViews();
                    Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                    List blueList = new ArrayList<HashMap>();
                    for (BluetoothDevice bluetoothDevice : devices) {
                        add(bluetoothDevice);
                    }

                    bluetoothAdapter.startDiscovery();
                } else {
                    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 1);
                    }
                }
                break;
            case R.id.button5:
                startActivity(new Intent(this, BluetoothServiceActivity.class));
                finish();
                break;
        }
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.i("配对结果", msg.obj.toString());
                    break;
                case 2:
                    Log.i("配对结果2", msg.obj.toString());
                    break;
                case 3:
                    Log.i("连接结果", msg.obj.toString());
                    break;
                case 8:
                    Log.i("读取结果", msg.obj.toString());
                    break;
            }

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                BluetoothLayout.removeAllViews();
                bluetoothAdapter.startDiscovery();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }

    class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.i("1", "开始扫描");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.i("1", "结束扫描");
                    break;
                case BluetoothDevice.ACTION_PAIRING_REQUEST:
                    Log.i("1", "开始配对");
                    BluetoothDevice bluetoothDevice3 = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    try {
//                        bluetoothDevice3.setPin("000000".getBytes());
                        Method method = bluetoothDevice3.getClass().getDeclaredMethod("setPairingConfirmation", boolean.class);
                        method.invoke(bluetoothDevice3, true);
//                        Method removeBondMethod = bluetoothDevice3.getClass().getDeclaredMethod("setPin", new Class[]{byte[].class});
//                        Boolean returnValue = (Boolean) removeBondMethod.invoke(bluetoothDevice3, new Object[]{new String("000000").getBytes("UTF-8")});

//                        handler.hasMessages(2, returnValue.booleanValue());
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }


                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    Log.i("1", "配对状态变化");

                    BluetoothDevice bluetoothDevice2 = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (bluetoothDevice2.getBondState()) {
                        case BluetoothDevice.BOND_NONE:
                            Log.d("配对状态变化", "取消配对");
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            Log.d("配对状态变化", "配对中");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            Log.d("配对状态变化", "配对成功");
                            if (Applymac.equals(bluetoothDevice2.getAddress()))
                                peidui(bluetoothDevice2);
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (bluetoothDevice != null) {
                        add(bluetoothDevice);
                    }
                    break;
            }
        }
    }

    private void add(BluetoothDevice bluetoothDevice) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("getName", bluetoothDevice.getName());
        jsonObject.put("getAddress", bluetoothDevice.getAddress());
        switch (bluetoothDevice.getBondState()) {
            case BluetoothDevice.BOND_NONE:
                //表示远程设备未绑定
                jsonObject.put("getBondState", "未配对");
                break;
            case BluetoothDevice.BOND_BONDING:
                //表示正在与远程设备进行绑定（配对）
                jsonObject.put("getBondState", "正在配对");
                break;
            case BluetoothDevice.BOND_BONDED:
                //表示远程设备已绑定（配对过,不一定正在连接）
                jsonObject.put("getBondState", "已配对");
                break;
        }
        switch (bluetoothDevice.getType()) {
            case 0:
                //Unknown
                //未知
                jsonObject.put("getType", "未知");
                break;
            case 1:
                //Classic - BR/EDR devices
                // 经典-br/edr设备
                jsonObject.put("getType", "br/edr");
                break;
            case 2:
                //Low Energy - LE-only
                //仅限低能耗-le
                jsonObject.put("getType", "LE-only");
                break;
            case 3:
                //Dual Mode - BR/EDR/LE
                //双模，BR/EDR/LE
                jsonObject.put("getType", "BR/EDR/LE");
                break;
        }
        jsonObject.put("getUuids", bluetoothDevice.getUuids());
        TextView textView = new TextView(BluetoothManageActivity.this);
        textView.setText(jsonObject.toJSONString());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                                if (bluetoothAdapter.isDiscovering())
//                                    bluetoothAdapter.cancelDiscovery();
                peidui(bluetoothDevice);
            }
        });
        BluetoothLayout.addView(textView);
    }

    private String Applymac = "";

    private void peidui(BluetoothDevice device) {
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            Method method = null;
            Applymac = device.getAddress();
            try {
                method = BluetoothDevice.class.getMethod("createBond");
                method.invoke(device);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Intent intent1 = new Intent(BluetoothManageActivity.this, BluetoothInfoDemo2Activity.class);
            intent1.putExtra("mac", device.getAddress());
            startActivity(intent1);
        }
    }
}