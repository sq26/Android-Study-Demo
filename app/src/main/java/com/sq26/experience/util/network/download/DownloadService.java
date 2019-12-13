package com.sq26.experience.util.network.download;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.sq26.experience.aidl.IDownloadServiceAidlInterface;
import com.sq26.experience.aidl.IDownloadServiceImpl;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadService extends Service {
    //跨进程通信接口
    private IDownloadServiceAidlInterface.Stub iDownloadService = new IDownloadServiceAidlInterface.Stub() {

        @Override
        public Map getDownloadInfo(String url) throws RemoteException {
            return null;
        }

        @Override
        public void setDownloadMap(Map downloadMap) throws RemoteException {

        }
    };
    //线程安全的hashMap
    private Map<String, Object> downloadMap = new ConcurrentHashMap<>();

    private Context context;

    //服务第一次启动时会调用这个方法
    @Override
    public void onCreate() {
        android.os.Debug.waitForDebugger();
        super.onCreate();
        context = this;
        try {
            iDownloadService.setDownloadMap(downloadMap);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //用startService方法启动服务会进入这个方法
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    //用bindService方法启动服务会进入这个方法
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        String url = intent.getStringExtra("url");
        //先插入数据,已存在是不会插入的
        DownloadHelperUtil.insert(this, url);
        //再将数据保存在下载集合中(已url做kay,不会重复)
        downloadMap.put(url, DownloadHelperUtil.query(context, url));
        //启动下载
        new StartDownload(url).download();

        try {
            Map map = new HashMap();
            map.put("bytes", 100);
            iDownloadService.setDownloadMap(downloadMap);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return iDownloadService;
    }

    //服务即将关闭前的回调
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class StartDownload {
        private String url;
        private Map map;
        //Range用于请求头中，指定第一个字节的位置和最后一个字节的位置，一般格式：
        //Range: bytes=0-499 表示第 0-499 字节范围的内容
        //Range: bytes=500-999 表示第 500-999 字节范围的内容
        //Range: bytes=-500 表示最后 500 字节的内容
        //Range: bytes=500- 表示从第 500 字节开始到文件结束部分的内容
        //Range: bytes=0-0,-1 表示第一个和最后一个字节
        //Range: bytes=500-600,601-999 同时指定几个范围
        private String range = "bytes=";
        private long already = 0;
        private long next = 1;
        private Long all = null;

        public StartDownload(String url) {
            this.url = url;
            this.map = (Map) downloadMap.get(url);
        }

        private void download() {

            OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Range", range + already + "-" + next)
                    .build();

            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    DownloadHelperUtil.updateStatus(context, url, Download.STATUS_ERROR);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //Content-Range用于响应头中，在发出带 Range 的请求后，服务器会在 Content-Range 头部返回当前接受的范围和文件总大小。
                    //Content-Range: bytes 0-499/22400
                    String contentRange = response.header("Content-Range");
                    all = Long.parseLong(contentRange.split("/")[1]);
                    byte[] bytes = response.body().bytes();

                    if (bytes.length == (next - already)) {
                        already += bytes.length;
                        if (next * 2 < all) {
                            next = next * 2;
                        } else {
                            next = all;
                        }
                    }

                    if (all == already) {

                    } else {
                        download();
                    }
                }
            });

        }
    }
}
