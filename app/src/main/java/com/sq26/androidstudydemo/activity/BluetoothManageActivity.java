package com.sq26.androidstudydemo.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.beacon.BeaconItem;
import com.inuker.bluetooth.library.beacon.BeaconParser;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.sq26.androidstudydemo.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.inuker.bluetooth.library.Code.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

public class BluetoothManageActivity extends AppCompatActivity {

    @BindView(R.id.BluetoothLayout)
    LinearLayout BluetoothLayout;
    BluetoothAdapter bluetoothAdapter;

//    BluetoothReceiver bluetoothReceiver;

    BluetoothClient mClient;
    Map<String, SearchResult> all;
    private List<SearchResult> list = new ArrayList<>();

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
        if (mClient != null)
            return;

        mClient = new BluetoothClient(this);
        if (!mClient.isBluetoothOpened())
            mClient.openBluetooth();
        //蓝牙状态监听
        mClient.registerBluetoothStateListener(new BluetoothStateListener() {
            @Override
            public void onBluetoothStateChanged(boolean openOrClosed) {
                if (openOrClosed) {
                    Log.i("蓝牙状态", "蓝牙已打开");
                } else {
                    Log.i("蓝牙状态", "蓝牙已关闭");
                }
            }
        });

        //监听设备配对状态变化
        mClient.registerBluetoothBondListener(mBluetoothBondListener);
        mClient.unregisterBluetoothBondListener(mBluetoothBondListener);


//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        bluetoothReceiver = new BluetoothReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
//        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
//        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//
//        registerReceiver(bluetoothReceiver, intentFilter);
//        if (!bluetoothAdapter.isEnabled()) {
//            bluetoothAdapter.enable();
//        }
    }

    private void updata() {
        BluetoothLayout.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            View inflate = LayoutInflater.from(BluetoothManageActivity.this).inflate(R.layout.item_bluetooth, null, false);
            TextView name = inflate.findViewById(R.id.name);
            TextView mac = inflate.findViewById(R.id.mac);
            TextView status = inflate.findViewById(R.id.status);
            name.setText(list.get(i).getName());
            mac.setText(list.get(i).getAddress());
            switch (list.get(i).device.getBondState()) {
                case BluetoothDevice.BOND_NONE:
                    //表示远程设备未绑定
                    status.setText("未配对");
                    break;
                case BluetoothDevice.BOND_BONDING:
                    //表示正在与远程设备进行绑定（配对）
                    status.setText("正在配对");
                    break;
                case BluetoothDevice.BOND_BONDED:
                    status.setText("已配对");
                    //表示远程设备已绑定（配对过,不一定正在连接）
                    break;
            }
            int finalI = i;
            inflate.findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connect(list.get(finalI).getAddress());
                }
            });
            inflate.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Beacon beacon = new Beacon(list.get(finalI).scanRecord);
                    if (beacon.mBytes != null){
                        Log.i("mBytes", new String(beacon.mBytes));
                    }
                    List<BeaconItem> mItems = beacon.mItems;
                    for (int i = 0; i < mItems.size(); i++) {
                        BeaconParser beaconParser = new BeaconParser(mItems.get(i));
                        beaconParser.setPosition(0);
//                        while (null != beaconParser.readByte()) {
//
//                            Log.i(""+beaconParser, new String(beacon.mBytes));
//                        }
                        int firstByte = beaconParser.readByte(); // 读取第1个字节
                        int secondByte = beaconParser.readByte(); // 读取第2个字节
                        int productId = beaconParser.readShort(); // 读取第3,4个字节

                        Log.i("firstByte", "" + firstByte);
                        Log.i("secondByte", "" + secondByte);
                        Log.i("productId", "" + productId);

//                        boolean bit1 = beaconParser.getBit(firstByte, 0); // 获取第1字节的第1bit
//                        boolean bit2 = beaconParser.getBit(firstByte, 1); // 获取第1字节的第2bit
                    }
                }
            });
            BluetoothLayout.addView(inflate);
        }
    }

    private void connect(String mac) {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)   // 连接如果失败重试3次
                .setConnectTimeout(30000)   // 连接超时30s
                .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
                .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
                .build();

        mClient.connect(mac, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                if (code == REQUEST_SUCCESS) {
//                    mClient.read(mac, serviceUUID, characterUUID, new BleReadResponse() {
//                        @Override
//                        public void onResponse(int code, byte[] data) {
//                            if (code == REQUEST_SUCCESS) {
//
//                            }
//                        }
//                    });
                }
            }
        });
        mClient.registerConnectStatusListener(mac, new BleConnectStatusListener() {
            @Override
            public void onConnectStatusChanged(String mac, int status) {
                if (status == STATUS_CONNECTED) {
                    Log.i("连接", "成功");
                } else if (status == STATUS_DISCONNECTED) {
                    Log.i("连接", "失败");

                }
            }
        });
    }


    private final BluetoothBondListener mBluetoothBondListener = new BluetoothBondListener() {
        @Override
        public void onBondStateChanged(String mac, int bondState) {
            // bondState = Constants.BOND_NONE, BOND_BONDING, BOND_BONDED
            switch (bondState) {
                case BluetoothDevice.BOND_NONE:
                    Log.d("配对状态变化", "取消配对");
                    break;
                case BluetoothDevice.BOND_BONDING:
                    Log.d("配对状态变化", "配对中");
                    break;
                case BluetoothDevice.BOND_BONDED:
                    Log.d("配对状态变化", "配对成功");

                    break;
            }
        }
    };

    @OnClick(R.id.button)
    public void onViewClicked() {

        SearchRequest request = new SearchRequest.Builder()
//                .searchBluetoothLeDevice(5000, 1)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
//                .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                .build();
        all = new HashMap<>();
        mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {

            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                if (device.getName() == null)
                    return;
                all.put(device.getAddress(), device);
                list = new ArrayList<>();
                for (SearchResult m : all.values()) {
                    list.add(m);
                }

            }

            @Override
            public void onSearchStopped() {

                updata();
            }

            @Override
            public void onSearchCanceled() {

            }
        });


//        if (bluetoothAdapter.isEnabled()) {
//            BluetoothLayout.removeAllViews();
//            bluetoothAdapter.startDiscovery();
//        } else {
//            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, 1);
//            }
//        }
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

//    private void pairBluetoothDevice(String address){
//        if (bluetoothAdapter.isDiscovering()) {
//            bluetoothAdapter.cancelDiscovery();
//        }
//        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
//        if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
//            try {
//                Method method = bluetoothDevice.getClass().getMethod("createBond");
//                Boolean b = (boolean) method.invoke(bluetoothDevice);
//
//                handler.hasMessages(1, b.booleanValue());
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//
//        } else if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
////            new ConnectBlueTask().execute(bluetoothDevice);
//            connectBluetoothDevice(bluetoothDevice);
//        }
//    }

//    private void connectBluetoothDevice(BluetoothDevice bluetoothDevice) {
//
//        bluetoothDevice.connectGatt(this, false, new BluetoothGattCallback() {
//            @Override//当连接上设备或者失去连接时会回调该函数
//            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
////                newState的意思,0是未连接没链接过,1是正在连接,2连接中,3已连接过现在是断开连接
//                Log.i("1status", status + "");
//                Log.i("1newState", newState + "");
////                Log.i("gatt",);
//                switch (newState) {
//                    case BluetoothProfile.STATE_DISCONNECTED:
//                        Log.i("1", "未连接没链接过");
//                        gatt.connect();
//                        break;
//                    case BluetoothProfile.STATE_CONNECTING:
//                        Log.i("1", "正在连接");
//                        break;
//                    case BluetoothProfile.STATE_CONNECTED:
//                        Log.i("1", "连接成功");
//                        SocketBluetoothDevice(gatt.getDevice());
//                        break;
//                    case BluetoothProfile.STATE_DISCONNECTING:
//                        Log.i("1", "已连接过现在是断开连接");
//                        break;
//                }
//            }
//
//            @Override
//            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//                super.onServicesDiscovered(gatt, status);
//                Log.i("2status", status + "");
//                if (status == BluetoothGatt.GATT_SUCCESS) {
//                    Log.i("2", "关联操作已完成");
//                }
//
//            }
//
//            @Override
//            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//                super.onCharacteristicRead(gatt, characteristic, status);
//                Log.i("3status", status + "");
//
//            }
//
//            @Override
//            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//                super.onCharacteristicChanged(gatt, characteristic);
//                Log.i("4status", new String(characteristic.getValue()));
//
//            }
//        });
//
//    }

//    class ConnectBlueTask extends AsyncTask<BluetoothDevice, Integer, BluetoothSocket> {
//        private BluetoothDevice bluetoothDevice;
//
//        @Override
//        protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevices) {
//            bluetoothDevice = bluetoothDevices[0];
//            BluetoothSocket socket = null;
//            try {
//                Method method = bluetoothDevice.getClass().getMethod("createRfcommSocket",new Class[]{int.class});
//                socket = (BluetoothSocket) method.invoke(bluetoothDevice,1);
////                UUID uuid = bluetoothDevice.getUuids()[0].getUuid();
////                socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
//                if (socket != null && !socket.isConnected()) {
//                    socket.connect();
//                }
//            } catch (IOException e) {
//                Log.e("socket", "socket连接失败");
//                handler.hasMessages(3, "socket连接失败");
//                try {
//                    socket.close();
//                } catch (IOException e1) {
//                    Log.e("socket", "socket关闭失败");
//                    e1.printStackTrace();
//                }
//                e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//            return socket;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            handler.hasMessages(3, "开始连接");
//        }
//
//        @Override
//        protected void onPostExecute(BluetoothSocket bluetoothSocket) {
//            if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
//                handler.hasMessages(4, "连接成功");
//            } else {
//                handler.hasMessages(5, "连接失败");
//            }
//
//        }
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (bluetoothReceiver != null)
//            unregisterReceiver(bluetoothReceiver);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
//                BluetoothLayout.removeAllViews();
//                bluetoothAdapter.startDiscovery();
                break;
        }
    }

//    class BluetoothReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            switch (action) {
//                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
//                    Log.i("1", "开始扫描");
//                    break;
//                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
//                    Log.i("1", "结束扫描");
//                    break;
//                case BluetoothDevice.ACTION_PAIRING_REQUEST:
//                    Log.i("1", "开始配对");
//                    BluetoothDevice bluetoothDevice3 = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    try {
////                        bluetoothDevice3.setPin("000000".getBytes());
//                        Method method = bluetoothDevice3.getClass().getDeclaredMethod("setPairingConfirmation", boolean.class);
//                        method.invoke(bluetoothDevice3, true);
////                        Method removeBondMethod = bluetoothDevice3.getClass().getDeclaredMethod("setPin", new Class[]{byte[].class});
////                        Boolean returnValue = (Boolean) removeBondMethod.invoke(bluetoothDevice3, new Object[]{new String("000000").getBytes("UTF-8")});
//
////                        handler.hasMessages(2, returnValue.booleanValue());
//                    } catch (NoSuchMethodException e) {
//                        e.printStackTrace();
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    break;
//                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
//                    Log.i("1", "配对状态变化");
//
//                    BluetoothDevice bluetoothDevice2 = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    switch (bluetoothDevice2.getBondState()) {
//                        case BluetoothDevice.BOND_NONE:
//                            Log.d("配对状态变化", "取消配对");
//                            break;
//                        case BluetoothDevice.BOND_BONDING:
//                            Log.d("配对状态变化", "配对中");
//                            break;
//                        case BluetoothDevice.BOND_BONDED:
//                            Log.d("配对状态变化", "配对成功");
////                            new ConnectBlueTask().execute(bluetoothDevice2);
//                            connectBluetoothDevice(bluetoothDevice2);
//                            break;
//                    }
//                    break;
//                case BluetoothDevice.ACTION_FOUND:
//                    BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    if (bluetoothDevice != null) {
//                        JSONObject jsonObject = new JSONObject();
//
//                        jsonObject.put("getName", bluetoothDevice.getName());
//                        jsonObject.put("getAddress", bluetoothDevice.getAddress());
//                        switch (bluetoothDevice.getBondState()) {
//                            case BluetoothDevice.BOND_NONE:
//                                //表示远程设备未绑定
//                                jsonObject.put("getBondState", "未配对");
//                                break;
//                            case BluetoothDevice.BOND_BONDING:
//                                //表示正在与远程设备进行绑定（配对）
//                                jsonObject.put("getBondState", "正在配对");
//                                break;
//                            case BluetoothDevice.BOND_BONDED:
//                                //表示远程设备已绑定（配对过,不一定正在连接）
//                                jsonObject.put("getBondState", "已配对");
//                                break;
//                        }
//                        switch (bluetoothDevice.getType()) {
//                            case 0:
//                                //Unknown
//                                //未知
//                                jsonObject.put("getType", "未知");
//                                break;
//                            case 1:
//                                //Classic - BR/EDR devices
//                                // 经典-br/edr设备
//                                jsonObject.put("getType", "br/edr");
//                                break;
//                            case 2:
//                                //Low Energy - LE-only
//                                //仅限低能耗-le
//                                jsonObject.put("getType", "LE-only");
//                                break;
//                            case 3:
//                                //Dual Mode - BR/EDR/LE
//                                //双模，BR/EDR/LE
//                                jsonObject.put("getType", "BR/EDR/LE");
//                                break;
//                        }
//                        jsonObject.put("getUuids", bluetoothDevice.getUuids());
//                        TextView textView = new TextView(BluetoothManageActivity.this);
//                        textView.setText(jsonObject.toJSONString());
//                        textView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                pairBluetoothDevice(bluetoothDevice.getAddress());
//                            }
//                        });
//                        BluetoothLayout.addView(textView);
//                    }
//                    break;
//            }
//        }
//    }

}
