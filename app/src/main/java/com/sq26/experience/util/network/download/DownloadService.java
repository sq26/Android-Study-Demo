package com.sq26.experience.util.network.download;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.sq26.experience.aidl.IDownloadServiceCallbackInterface;
import com.sq26.experience.aidl.IDownloadServiceInterface;

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
    private IDownloadServiceInterface.Stub iDownloadService = new IDownloadServiceInterface.Stub() {
        //获取当前下载的信息
        @Override
        public Map getDownloadInfo(String url) throws RemoteException {
            return null;
        }

        //注册下载进度回调
        @Override
        public void registerCallback(String url, IDownloadServiceCallbackInterface callback) throws RemoteException {
            downloadCallback.put(url, callback);
        }

        //解除进度回调
        @Override
        public void unregisterCallback(String url) throws RemoteException {
            downloadCallback.remove(url);
        }
    };

    private Map<String, IDownloadServiceCallbackInterface> downloadCallback = new ConcurrentHashMap<>();

    //线程安全的hashMap
    private Map<String, Map> downloadMap = new ConcurrentHashMap<>();

    private Context context;

    //服务第一次启动时会调用这个方法
    @Override
    public void onCreate() {
        android.os.Debug.waitForDebugger();
        super.onCreate();
        context = this;

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
        String path = intent.getStringExtra("path");
        //先插入数据,已存在是不会插入的
        DownloadHelperUtil.insert(this, url, path);
        //再将数据保存在下载集合中(用url做kay,不会重复)
        downloadMap.put(url, DownloadHelperUtil.query(context, url));
        //启动下载
        new StartDownload(url).download();

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
        private long already = 0;//下载起始位置(下标)
        private long next = 1;//下载结束位置(下标)
        private Long all = null;//要下载文件的总大小下载

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

                    if (bytes.length == next - already + 1) {
                        already += bytes.length;
                        next += 1024;
                        if (next > (all - 1))
                            next = (all - 1);
                    }

                    map.put("column_bytes", already + "");
                    map.put("column_total", all + "");
                    map.put("status", Download.STATUS_DOWNLOADING);

                    //判断有没有设置进度回调
                    if (downloadCallback.containsKey(url)) {
                        //返回当前进度
                        try {
                            downloadCallback.get(url).onProgress(downloadMap.get(url));
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    if (all == already) {
                        Log.d("1", "下载完成");
                    } else {
                        download();
                    }
                }
            });

        }
    }
}
