package com.sq26.experience.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.sq26.experience.R;
import com.sq26.experience.entity.ProgressEntity;
import com.sq26.experience.util.permissions.JPermissions;
import com.sq26.experience.util.permissions.PermissionUtil;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Thread.sleep;


/**
 * 写一个链式调用的下载管理工具
 */
public class DownloadManagement {

    //初始化,创建构造器
    public static Builder initialize(Context context) {
        return new Builder(context);
    }

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
        //下载完成后是否打开文件(默认下载完成后不打开文件)
        private boolean isOpenFile = false;
        //是否保存为私有文件(默认保存为公共文件)
        private boolean isSavePrivateFile = false;
        //文件下载位置(默认下载到私有文件夹)
        private String dirType = Environment.DIRECTORY_DOWNLOADS;
        //文件的保存名称(默认取下载链接中的文件名),文件名重复Android会自动在文件名后加字符不用担心
        private String fileName = null;
        //下载的网络类型(默认是移动网络和WIFI网络均可以，如果想只设置WIFI网络,设置DownloadManager.Request.NETWORK_WIFI)
        private int networkType = DownloadManager.Request.NETWORK_MOBILE;
        //是否允许漫游状态下，执行下载操作
        private boolean roaming = false;
        //是否允许“计量式的网络连接”执行下载操作
        private boolean allow = true;
        //设置Notification的显示,和隐藏(默认VISIBILITY_VISIBLE,如果没设置自动打开文件,默认值改为VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        //VISIBILITY_VISIBLE：通知显示，只是在下载任务执行的过程中显示，下载完成自动消失。（默认值）
        //VISIBILTY_HIDDEN: 通知将不会显示，如果设置该属性的话，必须要添加权限(Android.permission.DOWNLOAD_WITHOUT_NOTIFICATION. )
        //VISIBILITY_VISIBLE_NOTIFY_COMPLETED : 通知显示，下载进行时，和完成之后都会显示。
        //VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION ：下载完成时显示通知。
        private int visibility = DownloadManager.Request.VISIBILITY_VISIBLE;
        //下载文件的mimeType
        private String mimeType = null;
        //添加请求下载的网络链接的http头,比如User-Agent，gzip压缩等
        private Map<String, String> requestHeaders;
        //设置下载完成的回调
        private OnComplete onComplete;
        //设置实时下载进度的回调
        private OnProgress onProgress;
        //设置下载失败的回调
        private OnFailure onFailure;
        //下载id
        private Long downloadId;

        //构建
        Builder(Context context) {
            this.context = context;
        }

        //构建
        Builder(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        //设置下载链接
        public Builder setDownloadUrl(String url) {
            this.url = url;
            return this;
        }

        //设置自动打开文件(默认不打开)
        public Builder autoOpenFile(boolean isOpenFile) {
            this.isOpenFile = isOpenFile;
            return this;
        }

        //设置是否保存为私有文件(默认保存公共文件)
        public Builder whetherSavePrivateFile(boolean isSavePrivateFile) {
            this.isSavePrivateFile = isSavePrivateFile;
            return this;
        }

        //设置文件的下目录(默认下载到下载文件夹)
        public Builder setDownloadDirType(String dirType) {
            this.dirType = dirType;
            return this;
        }

        //设置文件名(要包含后缀)
        public Builder setDownloadFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        //设置下载的网络类型
        public Builder setAllowedNetworkTypes(int networkType) {
            this.networkType = networkType;
            return this;
        }

        //设置是否允许漫游状态下，执行下载操作
        public Builder setAllowedOverRoaming(boolean roaming) {
            this.roaming = roaming;
            return this;
        }

        //是否允许“计量式的网络连接”执行下载操作
        public Builder setAllowedOverMetered(boolean allow) {
            this.allow = allow;
            return this;
        }

        //设置Notification的显示,和隐藏
        public Builder setNotificationVisibility(int visibility) {
            this.visibility = visibility;
            return this;
        }

        //设置下载文件的MimeType
        public Builder setMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        //添加请求下载的网络链接的http头,比如User-Agent，gzip压缩等
        public Builder addRequestHeader(Map<String, String> requestHeaders) {
            this.requestHeaders = requestHeaders;
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
            //通过数据库查询这个链接的下载记录
            JSONObject jsonObject = DownloadManagementHelperUtil.query(context, url);
            //判断有没有下载过
            if (jsonObject == null) {
                //没有下载过
                //以下载链接创建下载记录
                DownloadManagementHelperUtil.insert(context, url);
                //申请权限
                requestPermissions();
            } else {
                //有下载过
                //判读有没有下载id
                if (jsonObject.getLong("id") != null) {
                    //判断当前下载计划的状态
                    //STATUS_FAILED下载失败（并不会重试）
                    //STATUS_PAUSED下载正在等待重试或恢复。
                    //STATUS_PENDING下载等待启动。
                    //STATUS_RUNNING正在运行下载。
                    //STATUS_SUCCESSFUL下载成功完成。
                    switch (getDownloadManagerStatus(context, jsonObject.getLong("id"))) {
                        //当下载失败（并不会重试）。
                        case DownloadManager.STATUS_FAILED:
                            //设置下载失败的回调
                            if (onFailure != null)
                                onFailure.failure(DownloadManager.STATUS_FAILED);
                            break;
                        //当下载正在等待重试或恢复。
                        case DownloadManager.STATUS_PAUSED:
                            //设置下载失败的回调
                            if (onFailure != null)
                                onFailure.failure(DownloadManager.STATUS_PAUSED);
                            break;
                        //当下载等待启动。
                        case DownloadManager.STATUS_PENDING:
                            AppUtil.showToast(context, R.string.File_download_waiting_to_start);
                            downloadId = jsonObject.getLong("id");
                            //开始进行实时查询
                            startProgress();
                            break;
                        //当前正在运行下载中。
                        case DownloadManager.STATUS_RUNNING:
                            AppUtil.showToast(context, R.string.File_Download_in_progress);
                            downloadId = jsonObject.getLong("id");
                            //开始进行实时查询
                            startProgress();
                            break;
                        //当下载成功完成。
                        case DownloadManager.STATUS_SUCCESSFUL:
                            //判断文件路径是否存在
                            if (FileUtil.isFileExists(jsonObject.getString("path"))) {
                                //存在
                                //判断是否自动打开文件
                                if (isOpenFile)
                                    //调用系统打开方式打开文件
                                    FileUtil.openFile(context, jsonObject.getString("path"));
                                //判断有没有设置完成回调
                                if (onComplete != null)
                                    //回调完成状态
                                    onComplete.complete(jsonObject.getString("path"));
                            } else {
                                //不存在
                                //判断系统下载记录里的文件是否存在
                                String path = getDownloadManagerFilePath(context, jsonObject.getLong("id"));
                                if (FileUtil.isFileExists(path)) {
                                    //存在
                                    //更新本地下载记录
                                    DownloadManagementHelperUtil.update(context, url, jsonObject.getLong("id"), path);
                                    //判断是否自动打开文件
                                    if (isOpenFile)
                                        //调用系统打开方式打开文件
                                        FileUtil.openFile(context, jsonObject.getString("path"));
                                    //判断有没有设置完成回调
                                    if (onComplete != null)
                                        //回调完成状态
                                        onComplete.complete(jsonObject.getString("path"));
                                } else {
                                    //不存在
                                    //申请权限
                                    requestPermissions();
                                }
                            }
                            break;
                        //其他情况
                        default:
                            //申请权限
                            requestPermissions();
                            break;
                    }
                } else {
                    //没有下载id,重下
                    //申请权限
                    requestPermissions();
                }
            }
            return this;
        }

        //申请权限
        private void requestPermissions() {
            //判断是否保存私有文件
            if (isSavePrivateFile) {
                //启动下载
                download();
            } else {
                //授权申请
                JPermissions.init((AppCompatActivity) context)
                        //申请存储权限
                        .permissions(PermissionUtil.Group.STORAGE)
                        //成功的回调
                        .success(new JPermissions.SuccessCallback() {
                            @Override
                            public void success() {
                                //启动下载
                                download();
                            }
                        })//失败的回调
                        .failure(new JPermissions.FailureCallback() {
                            @Override
                            public void failure(String[] successArray, String[] failureArray, String[] noPromptArray) {
                                //失败后弹出提示(暂时没想好怎么提示)
                            }
                        }).start();//开始
            }
        }

        //获取下载id
        private void download() {
            //把下载链接转化成Uri对象
            Uri uri = Uri.parse(url);
            //获取系统下载管理器
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            //创建下载请求
            DownloadManager.Request request = new DownloadManager.Request(uri);
            //判断有没有设置文件名
            if (fileName == null)
                //没有就从下载链接中取文件名
                fileName = FileUtil.getFileName(url);
            //判断是否保存为私有文件(私有文件会跟随app卸载而被删除,公共文件不会)
            if (isSavePrivateFile) {
                //保存为私有文件,目录: Android -> data -> app包名 -> files ->
                //设置下载为私有文件并设置目录和文件名
                request.setDestinationInExternalFilesDir(context, dirType, fileName);
            } else {
                //保存为公共文件,在SD卡上创建一个文件夹
                //设置下载为公共文件,并设置目录和文件名
                request.setDestinationInExternalPublicDir(dirType, fileName);
            }
            //设置下载网络类型,默认是移动网络和WIFI网络均可以
            request.setAllowedNetworkTypes(networkType);
            //是否允许漫游状态下，执行下载操作,默认否
            request.setAllowedOverRoaming(roaming);
            //是否允许“计量式的网络连接”执行下载操作,默认是允许
            request.setAllowedOverMetered(allow);
            //判断显示模式是不是 VISIBILITY_VISIBLE,是就说明这个是默认值,并且判断设置不自动打开,就进
            if (visibility == DownloadManager.Request.VISIBILITY_VISIBLE && !isOpenFile)
                //如果设置不自动打开就设置下载完成后不消失(避免下载完成后没有提示)
                visibility = DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
            //设置Notification的显示类型
            request.setNotificationVisibility(visibility);
            //判读有没有设置mimeType
            if (mimeType == null)
                //没有就根据文件名获取
                mimeType = FileUtil.getMimeType(fileName);
            //设置下载文件类型,有些机型必须设置此方法，才能在下载完成后，点击通知栏的Notification时,才能正确的打开可以打开对应文件的应用程序
            //无论设置不设置点击时系统都会试图打开文件
            request.setMimeType(mimeType);
            //判断有没有设置请求头
            if (requestHeaders != null)
                //遍历请求头
                for (String key : requestHeaders.keySet())
                    //添加请求头
                    request.addRequestHeader(key, requestHeaders.get(key));
            //启动下载并获取下载链接
            downloadId = downloadManager.enqueue(request);
            //更新一下下载id
            DownloadManagementHelperUtil.update(context, url, downloadId, "");
            //开始进行实时查询
            startProgress();
        }

        private void startProgress() {
            //获取系统下载管理器
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            //获取下载管理器的查询类
            DownloadManager.Query query = new DownloadManager.Query();
            //设置要查询的下载id
            query.setFilterById(downloadId);
            //开始实时查询下载状态
            Observable.create(new ObservableOnSubscribe<ProgressEntity>() {
                @Override
                public void subscribe(ObservableEmitter<ProgressEntity> emitter) throws Exception {
                    //判断循环是否继续
                    boolean isContinue = true;
                    //开始循环,间隔300毫秒一次
                    while (isContinue) {
                        //执行查询并获取游标
                        Cursor cursor = downloadManager.query(query);
                        //判断数据数量是否大于0
                        if (cursor.getCount() > 0) {
                            //大于零说明有数据
                            //把游标移动到第一条数据(这绝对只有一条数据,下载id是不可能重复的)
                            cursor.moveToPosition(0);
                            //到目前为止下载的字节数。
                            long COLUMN_BYTES_DOWNLOADED_SO_FAR = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            //下载的总大小（以字节为单位）。
                            long COLUMN_TOTAL_SIZE_BYTES = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                            //下载的当前状态，为STATUS_ *常量之一。
                            //STATUS_FAILED下载失败（并不会重试）
                            //STATUS_PAUSED下载正在等待重试或恢复。
                            //STATUS_PENDING下载等待启动。
                            //STATUS_RUNNING正在运行下载。
                            //STATUS_SUCCESSFUL下载成功完成。
                            int COLUMN_STATUS = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                            ProgressEntity progressEntity = new ProgressEntity();
                            progressEntity.setCurrent(COLUMN_BYTES_DOWNLOADED_SO_FAR);
                            progressEntity.setTotal(COLUMN_TOTAL_SIZE_BYTES);
                            emitter.onNext(progressEntity);
                            //判断下载状态是否是已完成状态
                            if (COLUMN_STATUS == DownloadManager.STATUS_SUCCESSFUL) {
                                //已完成就终止循环
                                isContinue = false;
                            } else if (COLUMN_STATUS == DownloadManager.STATUS_FAILED ||
                                    COLUMN_STATUS == DownloadManager.STATUS_PAUSED) {
                                //失败就终止循环
                                isContinue = false;
                            }
                        }
                        //关闭游标
                        cursor.close();
                        //暂停线程300毫秒
                        sleep(300);
                    }
                    //调用下载完成的回调
                    emitter.onComplete();
                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<ProgressEntity>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ProgressEntity progressEntity) {
                            //判读有没有设置实时下载监听
                            if (onProgress != null)
                                //回调实时下载监听
                                onProgress.progress(progressEntity.getCurrent(), progressEntity.getTotal());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            //执行查询并获取游标
                            Cursor cursor = downloadManager.query(query);
                            //判断数据数量是否大于0
                            if (cursor.getCount() > 0) {
                                //大于零说明有数据
                                //把游标移动到第一条数据(这绝对只有一条数据,下载id是不可能重复的)
                                cursor.moveToPosition(0);
                                //下载的当前状态，为STATUS_ *常量之一。
                                //STATUS_FAILED下载失败（并不会重试）
                                //STATUS_PAUSED下载正在等待重试或恢复。
                                //STATUS_PENDING下载等待启动。
                                //STATUS_RUNNING正在运行下载。
                                //STATUS_SUCCESSFUL下载成功完成。
                                int COLUMN_STATUS = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                                //判断是否下载完成
                                if (COLUMN_STATUS == DownloadManager.STATUS_SUCCESSFUL) {
                                    //获取文件的本地保存路径(文件路径有前缀,暴力替换感觉不行,暂时没有更好的方法)
                                    String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)).replace("file://", "");
                                    //更新下载记录
                                    DownloadManagementHelperUtil.update(context, url, downloadId, path);
                                    //判读有没有注册下载完成监听
                                    if (onComplete != null)
                                        //回调下载完成监听
                                        onComplete.complete(path);
                                    //判断是否打开文件
                                    if (isOpenFile) {
                                        //打开文件
                                        FileUtil.openFile(context, path);
                                    }
                                } else {
                                    //设置下载失败的回调
                                    if (onFailure != null)
                                        onFailure.failure(COLUMN_STATUS);
                                }
                            }
                        }
                    });
        }

        private void openCompleteReceiver() {
            //创建IntentFilter,添加action
            IntentFilter intentFilter = new IntentFilter();
            //添加下载完成动作
            intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            //添加点击下载NOTIFICATION的动作
            intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
            //这个猜想是打开加载管理列表,待验证
            intentFilter.addAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context1, Intent intent) {
                    //获取意图中的下载id
                    long extraDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    //判断广播类型
                    switch (intent.getAction()) {
                        //下载完成的广播(用户点击取消下载,也会发出这个广播)
                        case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                            //判断是不是申请的那个下载id
                            if (downloadId == extraDownloadId) {
                                //下载完成
                                //获取下载管理器的查询类
                                DownloadManager.Query query = new DownloadManager.Query();
                                //设置要查询的下载id
                                query.setFilterById(downloadId);
                                //设置要查询的下载状态,是下载完成状态
                                query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
                                //获取系统下载管理器
                                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                //执行查询并获取游标
                                Cursor cursor = downloadManager.query(query);
                                //判断数据数量是否大于0
                                if (cursor.getCount() > 0) {
                                    //大于零说明有数据
                                    //把游标移动到第一条数据(这绝对只有一条数据,下载id是不可能重复的)
                                    cursor.moveToPosition(0);
                                    //获取文件的本地保存路径(文件路径有前缀,暴力替换感觉不行,暂时没有更好的方法)
                                    String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)).replace("file://", "");
                                    //更新下载记录
                                    DownloadManagementHelperUtil.update(context, url, downloadId, path);
                                    //判断是否打开文件
                                    if (isOpenFile) {
                                        //打开文件
                                        FileUtil.openFile(context, path);
                                    }
                                }
                                //关闭游标
                                cursor.close();
                                //注销广播接收器
                                context.unregisterReceiver(this);
                            }
                            break;
                        case DownloadManager.ACTION_NOTIFICATION_CLICKED:
                            //点击下载的NOTIFICATION通知栏
                            //这里能获取到通知栏的点击但只在下载未完成时有效,下载完成后点不会再有此广播
                            Log.d("12", "点击了通知栏");
                            //判断是不是申请的那个下载id,并判断是不是保存再公共位置(是由位置没必要打开下载列表,反正也不会显示)
                            if (downloadId == extraDownloadId && !isSavePrivateFile)
                                //点击打开系统下载列表界面(再原生系统上没什么意义,再小米或魅族系统上有用)
                                context.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                            break;
                    }
                }
            };
            //注册广播接收器
            context.registerReceiver(broadcastReceiver, intentFilter);
        }

    }

    public static int getDownloadManagerStatus(Context context, Long downloadId) {
        //获取系统下载管理器
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //获取下载管理器的查询类
        DownloadManager.Query query = new DownloadManager.Query();
        //设置要查询的下载id
        query.setFilterById(downloadId);
//执行查询并获取游标
        Cursor cursor = downloadManager.query(query);
        //判断数据数量是否大于0
        if (cursor.getCount() > 0) {
            //大于零说明有数据
            //把游标移动到第一条数据(这绝对只有一条数据,下载id是不可能重复的)
            cursor.moveToPosition(0);
            //下载的当前状态，为STATUS_ *常量之一。
            //STATUS_FAILED下载失败（并不会重试）
            //STATUS_PAUSED下载正在等待重试或恢复。
            //STATUS_PENDING下载等待启动。
            //STATUS_RUNNING正在运行下载。
            //STATUS_SUCCESSFUL下载成功完成。
            return cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        }
        return 0;
    }

    public static String getDownloadManagerFilePath(Context context, Long downloadId) {
        //获取系统下载管理器
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //获取下载管理器的查询类
        DownloadManager.Query query = new DownloadManager.Query();
        //设置要查询的下载id
        query.setFilterById(downloadId);
//执行查询并获取游标
        Cursor cursor = downloadManager.query(query);
        //判断数据数量是否大于0
        if (cursor.getCount() > 0) {
            //大于零说明有数据
            //把游标移动到第一条数据(这绝对只有一条数据,下载id是不可能重复的)
            cursor.moveToPosition(0);
            //下载的当前状态，为STATUS_ *常量之一。
            //STATUS_FAILED下载失败（并不会重试）
            //STATUS_PAUSED下载正在等待重试或恢复。
            //STATUS_PENDING下载等待启动。
            //STATUS_RUNNING正在运行下载。
            //STATUS_SUCCESSFUL下载成功完成。

            //获取文件的本地保存路径(文件路径有前缀,暴力替换感觉不行,暂时没有更好的方法)
            return cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)).replace("file://", "");
        }
        return "";
    }

    //移除指定的下载任务
    public static boolean removeDownload(Context context, Long downloadId) {
        //获取系统下载管理器
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //移除任务
        if (downloadManager.remove(downloadId) == 1) {
            return true;
        } else {
            return false;
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
