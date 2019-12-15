package com.sq26.experience.util.network.download;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sq26.experience.aidl.IDownloadServiceCallbackInterface;
import com.sq26.experience.aidl.IDownloadServiceInterface;
import com.sq26.experience.util.DownloadManagement;
import com.sq26.experience.util.FileUtil;

import java.io.File;
import java.util.Map;

public class Download {
    //状态
    public static final int STATUS_COMPLETE = 1; //完成(下载成功)
    public static final int STATUS_ERROR = 2; //错误(下载出现错误404或其他)
    public static final int STATUS_DOWNLOADING = 3; //下载中(正在进行下载)
    public static final int STATUS_PAUSE = 4; //暂停(暂时停止下载)
    public static final int STATUS_START = 5; //准备开始(刚创建好记录,即将开始下载)

    //初始化,创建构造器
    public static Builder initialize(Context context, String url) {
        return new Builder(context, url);
    }

    //构造器类
    public static class Builder {
        //上下文
        private Context context;
        //文件下载路径
        private String url;
        //文件下载位置(默认下载到私有文件夹)
        private String dirPath = Environment.DIRECTORY_DOWNLOADS;
        //文件的保存名称(默认取下载链接中的文件名),文件名重复Android会自动在文件名后加字符不用担心
        private String fileName = null;
        //设置下载完成的回调
        private OnComplete onComplete;
        //设置实时下载进度的回调
        private OnProgress onProgress;
        //设置下载失败的回调
        private OnFailure onFailure;

        private IDownloadServiceInterface iDownloadServiceAidlInterface;

        //构建
        Builder(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        //设置文件的下目录(默认下载到下载文件夹)
        public Builder setDownloadDirPath(String dirPath) {
            this.dirPath = dirPath;
            return this;
        }

        //设置文件名(要包含后缀)
        public Builder setDownloadFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        //设置实时下载进度的监听
        public Builder setOnProgress(OnProgress onProgress) {
            this.onProgress = onProgress;
            return this;
        }

        //设置下载完成的监听
        public Builder setOnComplete(OnComplete onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        //设置下载完成的监听
        public Builder setOnFailure(OnComplete onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        //开始执行下载
        public Builder start() {
            //创建一个下载服务的显式意图
            Intent intent = new Intent(context, DownloadService.class);
            //设置下载地址
            intent.putExtra("url", url);

            //判断有没有设置文件名
            if (fileName == null)
                //没有就从下载链接中取文件名
                fileName = FileUtil.getFileName(url);
            //设置下载地址
            intent.putExtra("path", new File(dirPath, fileName).getAbsolutePath());
            //启动指定的服务
            context.startService(intent);
            //创建服务器主动调用客户端的回调
            IDownloadServiceCallbackInterface.Stub iDownloadServiceCallbackInterface = new IDownloadServiceCallbackInterface.Stub() {
                //这是做下载状态和下载进度的处理
                @Override
                public void onProgress(Map downloadMap) throws RemoteException {
                    Log.d("downloadMap", (String) downloadMap.get("column_bytes"));
                }
            };

            //创建服务通道
            ServiceConnection serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    //获取到服务器接口
                    iDownloadServiceAidlInterface = IDownloadServiceInterface.Stub.asInterface(iBinder);
                    try {
                        Map map = iDownloadServiceAidlInterface.getDownloadInfo(url);
                        //注册客户端的下载监听
                        iDownloadServiceAidlInterface.registerCallback(url, iDownloadServiceCallbackInterface);
//                        Log.d("map", map.get("bytes") + "");

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                //不带BIND_AUTO_CREATE标识的链接在服务器销毁前会收到此回调
                @Override
                public void onServiceDisconnected(ComponentName componentName) {

                }
            };


            /* 绑定一个服务
             * intent:绑定意图
             * serviceConnection:服务通道
             * flags: BIND_AUTO_CREATE:只要绑定存在就会自动创建这个 Service，虽然创建了 Service，但是它的 onStartCommand 方法是不会调用的，
             *                          因为这个方法只有在 startService 的时候被调用。
             *        BIND_DEBUG_UNBIND:用来 debug 使用的
             *        BIND_NOT_FOREGROUND:不允许将绑定的 Service 的进程提升到前台进程的优先级，它将仍然拥有和客户端同样的内存优先级，所以在宿主进程没有被杀死的情况下，Service 的进程也是不会被杀死的。但是 cpu 可能会把它放在后台执行。
             *        BIND_ABOVE_CLIENT:在这种情况下，Service 进程比 App 本身的进程还有重要，当设置后，内存溢出的时候，将会在关闭 Service 进程前关闭 App 进程。但是这种情况不能保证。
             *        BIND_ALLOW_OOM_MANAGEMENT:允许内存管理系统管理 Service 的进程，允许系统在内存不足的时候删除这个进程。
             *        BIND_WAIVE_PRIORITY:不影响 Service 进程的优先级的情况下，允许 Service 进程被加入后台队列中。
             *        BIND_IMPORITANT:这个服务对于这个客户端来说是非常重要的，所以应该提升到前台进程的级别。一般这个进程
                                      会提升到可见的级别，甚至客户端在后台的时候。
             *        BIND_ADJUST_WITH_ACTIVITY:如果从一个 Activity 绑定，则这个 Service 进程的优先级和 Activity 是否对用户可见有关。
             */
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


            //利用fragment来监听activity的关闭事件
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            DestroyFragment destroyFragment = new DestroyFragment(new DestroyFragment.DestroyCallback() {
                @Override
                public void destroy(DestroyFragment destroyFragment1) {
                    //解绑服务的回调
                    try {
                        iDownloadServiceAidlInterface.unregisterCallback(url);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    //在关闭时解绑服务
                    context.unbindService(serviceConnection);
                    //移除这个fragment
                    fragmentTransaction.remove(destroyFragment1);
                }
            });
            //加入fragment
            fragmentTransaction.add(destroyFragment, "fragment");
            return this;
        }
    }

    //下载完成的回调接口
    public interface OnComplete {
        //下载成功的回调方法
        abstract void complete(String path);
    }

    //下载失败的回调接口
    public interface OnFailure {
        //下载失败的回调方法
        abstract void failure(int status);
    }

    //实时下载进度的回调接口
    public interface OnProgress {
        //下载成功的回调方法
        abstract void progress(Long current, Long total);
    }
}
