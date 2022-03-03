package com.sq26.experience.util.network.download

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.*
import android.os.IBinder
import androidx.documentfile.provider.DocumentFile
import com.sq26.experience.app.OkHttpUtil
import com.sq26.experience.data.*
import com.sq26.experience.data.DownloadDao
import com.sq26.experience.util.Log
import com.sq26.experience.util.i
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.*
import java.io.File
import java.io.IOException
import java.util.concurrent.*
import javax.inject.Inject
import kotlin.concurrent.thread
import kotlin.math.min

@AndroidEntryPoint
class DownloadService : Service() {
    //声明下载表的操作类
    @Inject
    lateinit var downloadDao: DownloadDao

    //service是否已关闭
    private var isDestroy = false

    private var netWorkStatus = 0//0无网络,1移动网络,2wifi网络

    //服务第一次启动时会调用这个方法
    override fun onCreate() {
        isDestroy = false
        Log.i("启动", "服务")
//        Debug.waitForDebugger()
        super.onCreate()

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(),
            object : ConnectivityManager.NetworkCallback() {
                //网络不可用时回调
                override fun onLost(network: Network) {
                    super.onLost(network)
                    netWorkStatus = 0
                    "网络不可用".i()
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        //有网络
                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                            netWorkStatus = 2
                            "WIFI".i()
                        } else {
                            netWorkStatus = 1
                            "移动".i()
                        }
                    }
                }
            })

    }

    //用startService方法启动服务会进入这个方法
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        thread {
            //更新任务
            update()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    //获取剩余可用内存
    private fun totalFree(): Long {
        //获取页面服务
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        //申明内存详情
        val memoryInfo = ActivityManager.MemoryInfo()
        //获取内存详情
        activityManager.getMemoryInfo(memoryInfo)
        //获取jvm运行时状态
        val runtime = Runtime.getRuntime()
        //取最小值
        return min(
            //jvm剩余可用内存 = 最大可使用内存-已使用内存(全部已使用内存-可回收内存)
            (runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory())),
            //系统物理剩余内存
            memoryInfo.availMem
        )
    }

    //1MB内存
    private val mb1 = 1024 * 1024

    private fun initDownload(id: Long) {
        //判断有网络进入
        if (netWorkStatus != 0)
        //获取http客户端
        //获取一个新的通信
            OkHttpUtil.getInstance().newCall(
                //使用head请求,只获取请求头
                Request.Builder()
                    .head()
                    .url(downloadDao.getDownloadForId(id).url)
                    .build()
                //发起请求通信并设置异步回调
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    //有网络进入
                    if (netWorkStatus != 0) {
                        val errorFrequency = downloadDao.getDownloadForId(id).errorFrequency
                        //判断失败次数
                        if (errorFrequency < 3) {
                            //记录下载失败次数
                            downloadDao.updateErrorFrequency(
                                DownloadEntityErrorFrequency(
                                    id,
                                    errorFrequency + 1
                                )
                            )
                            //重试
                            initDownload(id)
                        } else {
                            //修改下载状态为失败
                            downloadDao.updateStatus(
                                DownloadEntityStatus(
                                    id,
                                    DownloadStatus.ERROR
                                )
                            )
                        }
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    //Content-Range用于响应头中，在发出带 Range 的请求后，服务器会在 Content-Range 头部返回当前接受的范围和文件总大小。
                    //Content-Range: bytes 0-499/22400
                    //判断请求是否成功
                    if (response.code == 200 || response.code == 206) {
                        //获取要下载的文件大小
                        val size = response.header("Content-Length", "0")?.toLong()!!
                        //通过判断是否第一次下载
                        if (downloadDao.getDownloadForId(id).size > 0) {
                            //判断要下载的文件是否已变化
                            //判断保存的大小和现在的大小是否一致
                            if (size != downloadDao.getDownloadForId(id).size) {
                                //不一致,文件已改变需要清除之前的数据
//                            val documentFile = DocumentFile.fromSingleUri(
//                                applicationContext,
//                                Uri.parse(downloadDao.getDownloadForId(id).fileUri)
//                            )
                            } else {
                                //修改下载状态为等待
                                downloadDao.updateStatus(
                                    DownloadEntityStatus(
                                        id,
                                        DownloadStatus.WAIT
                                    )
                                )
//                            //更新下载任务
                                update()
                                return
                            }
                        }
                        //设置文件大小
                        downloadDao.updateSize(DownloadEntitySize(id, size))

                        if (totalFree() < mb1) {
                            Log.i("内存不足")
                            return
                        }

                        val outputStream =
                            contentResolver.openOutputStream(
                                Uri.parse(
                                    downloadDao.getDownloadForId(
                                        id
                                    ).fileUri
                                )
                            )

                        val byteArray = ByteArray(mb1)

                        var i = 0

                        while (i < size) {
                            val count: Int =
                                if (mb1 < (size - i)) mb1 else (size - i).toInt()
                            outputStream?.write(byteArray, 0, count)
                            i += count
                        }
                        outputStream?.flush()
                        outputStream?.close()

                        //数据填充完成,把下载状态修改为等待下载,如果状态时暂停就不修改状态
                        if (downloadDao.getDownloadForId(id).status != DownloadStatus.PAUSE)
                            downloadDao.updateStatus(
                                DownloadEntityStatus(id, DownloadStatus.WAIT)
                            )
                        else
                        //是暂停判断是否删除
                            delete(id)
                        //判断有无网络
                        if (netWorkStatus != 0)
                        //更新下载任务
                            update()
                    } else {
                        //判断有无网络
                        if (netWorkStatus != 0) {
                            //判断失败次数
                            val errorFrequency = downloadDao.getDownloadForId(id).errorFrequency
                            if (errorFrequency < 3) {
                                //记录下载失败次数
                                downloadDao.updateErrorFrequency(
                                    DownloadEntityErrorFrequency(
                                        id,
                                        errorFrequency + 1
                                    )
                                )
                                //重试
                                initDownload(id)
                            } else {
                                //修改下载状态为失败
                                downloadDao.updateStatus(
                                    DownloadEntityStatus(
                                        id,
                                        DownloadStatus.ERROR
                                    )
                                )
                                //更新下载任务
                                update()
                            }
                        }
                    }
                }
            })
    }

    private fun update() {
        //更新初始化任务
        updateInitDownloadTask()
        //更新下载任务
        updateDownloadTask()
        //更新删除任务
        updateDeleteTask()
    }

    //更新下载任务
    private fun updateDownloadTask() {
        Log.i("更新下载任务")
        //获取可自动开始的未完成下载列表
        val downloadEntityList = downloadDao.getUndoneDownloadList()
        Log.i("下载任务$downloadEntityList")
        //遍历可下载的任务
        for (item in downloadEntityList) {
            //判断正在执行的任务数是否到达3个
            if (downloadDao.getDownloadForStatus(DownloadStatus.DOWNLOADING) < 5) {
                //没有到达就添加新的下载
                //判断有没有暂停
                if (downloadDao.getDownloadForId(item.id).status != DownloadStatus.PAUSE) {
                    //将状态设置为下载中
                    downloadDao.updateStatus(
                        DownloadEntityStatus(
                            item.id,
                            DownloadStatus.DOWNLOADING
                        )
                    )
                    //开始下载任务
                    Log.i("开始下载任务")
                    thread {
                        startDownload(item.id)
                    }
                }
            } else {
                //已经到了就结束调用
                return
            }
        }
    }

    //更新初始化任务
    private fun updateInitDownloadTask() {
        //获取可自动开始的未完成下载列表
        val downloadEntityList = downloadDao.getUndoneInitDownloadList()
        //遍历可下载的任务
        for (item in downloadEntityList) {
            //判断是否有正在初始化的数据
            if (downloadDao.getDownloadForStatus(DownloadStatus.INIT_ING) < 1) {
                //没有到达就添加新的下载
                //判断有没有暂停
                if (downloadDao.getDownloadForId(item.id).status != DownloadStatus.PAUSE) {
                    //将状态设置为初始化中
                    downloadDao.updateStatus(
                        DownloadEntityStatus(
                            item.id,
                            DownloadStatus.INIT_ING
                        )
                    )
                    //开始初始化
                    Log.i("开始初始化")
                    thread {
                        initDownload(item.id)
                    }
                }
            } else {
                //已经到了就结束调用
                return
            }
        }
    }


    private fun startDownload(id: Long) {
        val downloadEntity = downloadDao.getDownloadForId(id)
        //创建一个请求
        //Range用于请求头中，指定第一个字节的位置和最后一个字节的位置，一般格式：
        //Range: bytes=0-499 表示第 0-499 字节范围的内容
        //Range: bytes=500-999 表示第 500-999 字节范围的内容
        //Range: bytes=-500 表示最后 500 字节的内容
        //Range: bytes=500- 表示从第 500 字节开始到文件结束部分的内容
        //Range: bytes=0-0,-1 表示第一个和最后一个字节
        //Range: bytes=500-600,601-999 同时指定几个范围
        val request = Request.Builder()
            .url(downloadEntity.url)
            //指定获取开始字节和结束字节
            .addHeader(
                "Range",
                "bytes=${downloadEntity.alreadySize}-"
            )
            .build()
//        //获取通信
        val call = OkHttpUtil.getInstance()
            .newBuilder()
            //连接超时时间20秒
            .connectTimeout(20, TimeUnit.SECONDS)
            //写入超时时间
            .writeTimeout(20, TimeUnit.SECONDS)
            //通讯超时时间20秒
            .callTimeout(20, TimeUnit.SECONDS)
            //读取超时30秒
            .readTimeout(20, TimeUnit.SECONDS)
            //设置不自动重试
            .retryOnConnectionFailure(false)
            .build()
            .newCall(request)
//        //发起请求获取response
        val response = call.execute()

        if (response.code == 200 || response.code == 206) {
            //判断剩余内存大于2兆
            if (totalFree() > mb1 * 2) {
                //获取输出流
                val outputStream =
                    contentResolver.openOutputStream(Uri.parse(downloadEntity.fileUri))
                //判断已下载大小大于0
                if (downloadEntity.alreadySize > 0) {
                    //声明byte数组
                    val byteArray = ByteArray(mb1)
                    //获取输入流
                    val inputStream =
                        contentResolver.openInputStream(Uri.parse(downloadEntity.fileUri))
                    //游标
                    var i = 0
                    //判断游标小于已下载大小就一直循环
                    while (i < downloadEntity.alreadySize) {
                        //将输入流的数据写入byte数组
                        inputStream?.read(byteArray)
                        //获取本次的写数量,已下载大小减去游标,判断是否大于单次的写入数量,大于就取单次的写入数量,小于就取已下载数量减去游标数的值就是最后的写入量
                        val count: Int =
                            if (mb1 < (downloadEntity.alreadySize - i)) mb1 else (downloadEntity.alreadySize - i).toInt()
                        //写入输出流
                        outputStream?.write(byteArray, 0, count)
                        //游标数叠加本次写入量
                        i += count
                    }
                    //关闭输入流
                    inputStream?.close()
                }

                //获取response的输入流
                val inputStream = response.body?.byteStream()
                //申明字节数组
                val bytes = ByteArray(8192)
                //声明单次获取长度
                var len = 0
                //申明已下载大小
                var sum = downloadEntity.alreadySize
                //单次获取大小不等与-1,循环继续
                while (len != -1) {
                    //获取单次下载大小
                    len = try {
                        inputStream?.read(bytes) ?: -1
                    } catch (e: Exception) {
                        e.printStackTrace()
                        //socket异常,手动调用取消请求引起
                        "socket异常,手动调用取消请求引起".i()
                        -1
                    }
//                catch (e: SocketTimeoutException) {
//                    //读取超时导致
//                    Log.i("SocketTimeoutException读取超时导致")
//                    -1
//                } catch (e: InterruptedIOException) {
//                    //线程超时
//                    Log.i("InterruptedIOException线程超时")
//                    -1
//                }

                    //判断单次大小不等于-1时继续
                    if (len != -1) {
                        //写入到文件输出流,bytes字节流,bytes开始位置,len是bytes结束位置,这里不能取完整的bytes,因为完整的bytes写死了4096的大小
                        outputStream?.write(bytes, 0, len)
                        //累加已下载大小
                        sum += len
                        //设置已下载大小,并重置失败次数
                        downloadDao.updateAlreadySize(DownloadEntityAlreadySize(id, sum))
                        //判断是否主动暂停
                        if (downloadDao.getDownloadForId(downloadEntity.id).status == DownloadStatus.PAUSE) {
                            call.cancel()
                        }
                    }
                }

//            //把缓冲区的数据强行输出
                outputStream?.flush()
                //关闭输出流
                outputStream?.close()
                //关闭输入流
                inputStream?.close()
                //判断任务是否暂停
                if (downloadDao.getDownloadForId(id).status == DownloadStatus.PAUSE) {
                    //更新下载任务,开始下载另一个任务
                    updateDownloadTask()
                    //是暂停判断是否删除
                    delete(id)
                } else if (netWorkStatus == 0) {
                    downloadDao.updateStatus(DownloadEntityStatus(id, DownloadStatus.AUTO_PAUSE))
                } else if (len == -1 && downloadDao.getDownloadForId(id).alreadySize != downloadEntity.size) {
                    //判断失败次数
                    val errorFrequency = downloadDao.getDownloadForId(id).errorFrequency
                    if (errorFrequency < 3) {
                        //记录下载失败次数
                        downloadDao.updateErrorFrequency(
                            DownloadEntityErrorFrequency(
                                id,
                                errorFrequency + 1
                            )
                        )
                        //重试
                        startDownload(id)
                    } else {
                        //修改下载状态为失败
                        downloadDao.updateStatus(DownloadEntityStatus(id, DownloadStatus.ERROR))
                        //更新下载任务,开始下载另一个任务
                        updateDownloadTask()
                    }
                } else {
                    //下载完毕
                    //修改下载状态为失败
                    downloadDao.updateStatus(DownloadEntityStatus(id, DownloadStatus.COMPLETE))
                    //更新下载任务,开始下载另一个任务
                    updateDownloadTask()
                }
            } else {
                Log.i("剩余内存不足")
            }
        } else {
            //判断有网络进
            if (netWorkStatus != 0) {
                //判断失败次数
                val errorFrequency = downloadDao.getDownloadForId(id).errorFrequency
                if (errorFrequency < 3) {
                    //记录下载失败次数
                    downloadDao.updateErrorFrequency(
                        DownloadEntityErrorFrequency(
                            id,
                            errorFrequency + 1
                        )
                    )
                    //重试
                    startDownload(id)
                } else {
                    //修改下载状态为失败
                    downloadDao.updateStatus(DownloadEntityStatus(id, DownloadStatus.ERROR))
                    //更新下载任务,开始下载另一个任务
                    updateDownloadTask()
                }
            }
        }
    }

    //删除
    private fun delete(id: Long) {
        //获取下载信息
        val downloadEntity = downloadDao.getDownloadForId(id)
        //判断是否发起删除
        if (downloadEntity.deleteFlag) {
            //判断是否保留文件
            if (!downloadEntity.isKeepFile) {
                //删除文件
                val uri = Uri.parse(downloadEntity.fileUri)
                if (uri.scheme == "file") {
                    if (DocumentFile.fromFile(File(Uri.parse(downloadEntity.fileUri).path ?: ""))
                            .delete()
                    ) {
                        "文件已删除".i()
                        downloadDao.updateIsDelete(DownloadEntityIsDelete(id))
                    } else {
                        "文件删除失败".i()
                        downloadDao.updateIsDelete(DownloadEntityIsDelete(id))
                    }
                } else {
                    if (DocumentFile.fromSingleUri(
                            applicationContext,
                            Uri.parse(downloadEntity.fileUri)
                        )
                            ?.delete() == true
                    ) {
                        "文件已删除".i()
                        downloadDao.updateIsDelete(DownloadEntityIsDelete(id))
                    } else {
                        "文件删除失败".i()
                        downloadDao.updateIsDelete(DownloadEntityIsDelete(id))
                    }
                }
            } else {
                "可以删除了".i()
                downloadDao.updateIsDelete(DownloadEntityIsDelete(id))
            }
        }
    }

    //更新初始化任务
    private fun updateDeleteTask() {
        //获取已结束并打上删除标志的列表
        val downloadEntityList = downloadDao.getDeleteFlagList()
        //遍历可下载的任务
        for (item in downloadEntityList) {
            //删除
            delete(item.id)
        }
    }

    //服务即将关闭前的回调
    override fun onDestroy() {
        isDestroy = true
        "关闭前".i("服务")
        super.onDestroy()
        "关闭后".i("服务")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}

