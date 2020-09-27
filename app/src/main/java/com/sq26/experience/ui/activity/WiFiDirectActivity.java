package com.sq26.experience.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.sq26.experience.R;
import com.sq26.experience.adapter.CommonAdapter;
import com.sq26.experience.adapter.RecyclerViewListAdapter;
import com.sq26.experience.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WiFiDirectActivity extends AppCompatActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private Context context;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private WiFiDirectReceiver wiFiDirectReceiver;
    private RecyclerViewListAdapter<Map<String, Object>> recyclerViewListAdapter;
    private List<Map<String, Object>> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_direct);
        ButterKnife.bind(this);
        context = this;
        //获取wifi管理器
        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        //channel是wifi和应用间的通道
        channel = wifiP2pManager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                //链接断开监听
            }
        });

        recyclerViewListAdapter = new RecyclerViewListAdapter<Map<String, Object>>(R.layout.item_recyclerview, list) {
            @Override
            protected void bindViewHolder(ViewHolder viewHolder, Map<String, Object> item, int position) {
                WifiP2pDevice device = (WifiP2pDevice) item.get("device");
                viewHolder.setText(R.id.text, "deviceName:" + device.deviceName + "\n" +
                        "deviceAddress:" + device.deviceAddress + "\n" +
                        "status:" + device.status + "\n");

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
                        wifiP2pConfig.deviceAddress = device.deviceAddress;
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        }
                        wifiP2pManager.connect(channel, wifiP2pConfig, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure(int reason) {

                            }
                        });
                    }
                });
            }
        };
        recyclerView.setAdapter(recyclerViewListAdapter);

        wiFiDirectReceiver = new WiFiDirectReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        registerReceiver(wiFiDirectReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(wiFiDirectReceiver);
    }

    class WiFiDirectReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("tag", "===============wifi direct action: " + action);
            if (action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)) {
                //获取wifi状态
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    //打开
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    //发起设备检索
                    wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            //发现设备
                        }

                        @Override
                        public void onFailure(int reason) {

                        }
                    });
                } else if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                    //关闭
                }
            } else if (action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)) {
                //发现设备
                //获取所有设备
                wifiP2pManager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        Log.i("device size", peers.getDeviceList().size() + "");
                        list.clear();
                        for (WifiP2pDevice device : peers.getDeviceList()) {
                            Log.i("device addr", device.deviceAddress);
                            Log.i("device name", device.deviceName);
                            Map<String, Object> map = new HashMap<>();
                            map.put("device", device);
                            list.add(map);
                        }
                        recyclerViewListAdapter.notifyDataSetChanged();
                    }
                });
            } else if (action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)) {

            } else if (action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)) {

            }
        }

    }
}