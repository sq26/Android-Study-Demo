package com.sq26.experience.util.network.download

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.text.format.Formatter
import android.util.Log
import com.orhanobut.logger.Logger
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.*

class DownloadService : Service() {
    //声明下载表的操作类
    private var downloadDao: DownloadDao? = null

    //声明下载切片表的操作类
    private var downloadSliceDao: DownloadSliceDao? = null

    //服务第一次启动时会调用这个方法
    override fun onCreate() {
        Log.i("服务", "启动")
//        Debug.waitForDebugger()
        super.onCreate()
        downloadDao = DownloadDatabase.getInstance(applicationContext).getDownloadDao()
        downloadSliceDao = DownloadDatabase.getInstance(applicationContext).getDownloadSliceDao()
        Thread {
            updateDownloadTask()
        }.start()
    }

    //用startService方法启动服务会进入这个方法
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Thread {
            //获取下载信息
            val downloadEntity = downloadDao?.getDownloadForId(intent.getIntExtra("id", 0))
            Logger.i("status:${downloadEntity?.status}")
            //已经创建过
            when (downloadEntity?.status) {
                //刚创建,维修完,错误是需要重新切片的,下载完的切片任务也被删除了
                Download.STATUS_CREATE,
                Download.STATUS_COMPLETE,
                Download.STATUS_ERROR -> {
                    //去切片
                    compareAndSlice(downloadEntity)
                }
                //等待状态,自动暂停
                Download.STATUS_WAIT,
                Download.STATUS_AUTO_PAUSE -> {
                    //更新下载任务
                    updateDownloadTask()
                }
                //暂停
                Download.STATUS_PAUSE -> {
                    //将状态改为准备下载状态
                    downloadEntity.status = Download.STATUS_WAIT
                    //更新状态
                    downloadDao?.updateItem(downloadEntity)
                    //更新下载任务
                    updateDownloadTask()
                }
            }
        }.start()
        return super.onStartCommand(intent, flags, startId)
    }

    //比较并切片数据,用于测试连接是否可用,以及获取文件大小,比较数据将数据切片标记
    fun compareAndSlice(downloadEntity: DownloadEntity) {
        //创建http客户端
        val okHttpClient = OkHttpClientBuilder.getInstance()
        //创建一个请求
        //使用head请求,只获取请求头
        val request = Request.Builder()
                .head()
                .url(downloadEntity.url)
                .build()
        //获取一个新的通信
        val call = okHttpClient.newCall(request)
        //发起请求通信并设置回调
        call.enqueue(object : Callback {
            //失败
            override fun onFailure(call: Call, e: IOException) {
                //修改下载状态为失败
                downloadEntity.status = Download.STATUS_ERROR
                //更新下载详情
                downloadDao?.updateItem(downloadEntity)
            }

            //成功
            override fun onResponse(call: Call, response: Response) {
                //Content-Range用于响应头中，在发出带 Range 的请求后，服务器会在 Content-Range 头部返回当前接受的范围和文件总大小。
                //Content-Range: bytes 0-499/22400
                Logger.i(response.headers.toString())
                //获取要下载的文件大小
                val size = response.header("Content-Length", "0")?.toLong()!!
                //判断要下载的文件是否已变化
                //通过判断是否第一次下载
                if (downloadEntity.status != Download.STATUS_CREATE) {
                    //判断保存的大小和现在的大小是否一致
                    if (size != downloadEntity.size)
                    //不一致,文件已改变需要清除之前的切片数据
                        downloadSliceDao?.deleteAllItem(downloadSliceDao?.getListForDownloadId(downloadEntity.id!!)
                                ?: listOf())
                    //判断有没有切片数据
                    if (downloadSliceDao?.getListForDownloadId(downloadEntity.id!!)?.size ?: 0 > 0) {
                        //有切片数据不用重写切片
                        //状态改为等待下载
                        downloadEntity.status = Download.STATUS_WAIT
                        //刷新状态
                        downloadDao?.updateItem(downloadEntity)
                        //更新下载任务
                        updateDownloadTask()
                        return
                    }
                }
                //设置文件大小
                downloadEntity.size = size
                //单片大小10MB
                val MB10 = 10485760
                //切片开始下标,10485760(10MB一片)
                var startIndex: Long = 0
                //声明一个切片数据数组
                val downloadSliceList = mutableListOf<DownloadSliceEntity>()
                //申明切片对象
                var downloadSliceEntity: DownloadSliceEntity?
                //循环判断切片,当开始下标不大于文件大小的时候一直循环
                while (startIndex < size) {
                    //初始化切片对象
                    downloadSliceEntity = DownloadSliceEntity()
                    //设置下载id
                    downloadSliceEntity.downloadId = downloadEntity.id!!
                    //设置切片开始下标
                    downloadSliceEntity.startIndex = startIndex
                    //判断开始下标加10MB字节是否大于总大小
                    if (startIndex + MB10 > size) {
                        //大于总大小说明已经切片完毕
                        //设置切片结束下标,为总大小
                        downloadSliceEntity.endIndex = size - 1
                    } else {
                        //小于中大小,说明还没切完
                        //设置切片技术下标,为开始下标加一百万
                        downloadSliceEntity.endIndex = startIndex + MB10 - 1
                    }
                    //将当前切片加入切片列表
                    downloadSliceList.add(downloadSliceEntity)
                    //将切片开始下标提高一百万
                    startIndex += MB10
                }
                //把切片列表加入数据库
                downloadSliceDao?.addAllItem(downloadSliceList)
                //切片完成,把下载状态修改为等待下载
                downloadEntity.status = Download.STATUS_WAIT
                //更新下载信息
                downloadDao?.updateItem(downloadEntity)
                //更新下载任务
                updateDownloadTask()
            }
        })
    }

    //下载切片
    fun downloadSlice(downloadSliceEntity: DownloadSliceEntity, downloadEntity: DownloadEntity) {
        //获取okHttpClient客户端
        val okHttpClient = OkHttpClientBuilder.getInstance()
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
                .addHeader("Range", "bytes=${downloadSliceEntity.startIndex + downloadSliceEntity.alreadySize}-${downloadSliceEntity.endIndex}")
                .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //下载失败
                //将下载任务状态修改为下载失败
                downloadEntity.status = Download.STATUS_ERROR
                //更新数据库下载任务数据
                downloadDao?.updateItem(downloadEntity)
                //将下载任务修改为未完成
                downloadSliceEntity.status = Download.SLICE_STATUS_NOT_FINISHED
                //更新数据库切片数据
                downloadSliceDao?.updateItem(downloadSliceEntity)
                //更新下载任务,开始下载另一个任务
                updateDownloadTask()
            }

            override fun onResponse(call: Call, response: Response) {
                //判断有没有创建过文件路径
                if (downloadSliceEntity.filePath.isEmpty())
                //没创建过,创建文件路径
                    downloadSliceEntity.filePath = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), System.currentTimeMillis().toString() + downloadSliceEntity.id).absolutePath
                //声明file
                val file = File(downloadSliceEntity.filePath)
                //判断文件是否存在
                if (!file.exists())
                //不存在就创建新文件
                    file.createNewFile()
                //获取文件输出流
                val fileOutputStream = FileOutputStream(file)
                //获取response的输入流
                val inputStream = response.body?.byteStream()
                //申明字节数组
                val bytes = ByteArray(4096)
                //声明单次获取长度
                var len = 0
                //申明已下载大小
                var sum = 0L
                //单次获取大小不等与-1,循环继续
                while (len != -1) {
                    //获取单次下载大小
                    len = inputStream?.read(bytes) ?: -1
                    //判断单次大小不等于-1时继续
                    if (len != -1) {
                        //写入到文件输出流,bytes字节流,bytes开始位置,len是bytes结束位置,这里不能取完整的bytes,因为完整的bytes写死了4096的大小
                        fileOutputStream.write(bytes, 0, len)
                        //累加已下载大小
                        sum += len
                        //设置已下载大小
                        downloadSliceEntity.alreadySize = sum
                        //更新数据库切片数据
                        downloadSliceDao?.updateItem(downloadSliceEntity)
                    }
                }
                fileOutputStream.flush()
                //关闭文件输出流
                fileOutputStream.close()
                //关闭输入流
                inputStream?.close()
                //设置状态为已完成
                downloadSliceEntity.status = Download.SLICE_STATUS_COMPLETE
                //更新数据库切片数据
                downloadSliceDao?.updateItem(downloadSliceEntity)
                //检查并下载新的切片
                startDownload(downloadEntity)
            }
        })
    }

    //启动下载
    fun startDownload(downloadEntity: DownloadEntity) {
        //获取所有未完成下载
        val downloadSliceEntityList = downloadSliceDao?.getListForDownloadIdAndStatus(downloadEntity.id!!, Download.SLICE_STATUS_NOT_FINISHED)
        //判断还有没有未下载切片
        if (downloadSliceEntityList?.size ?: 0 > 0) {
            for (item in downloadSliceEntityList!!) {
                //判断当前下载的切片任务数量是否小于3
                if (downloadSliceDao?.getListSizeForDownloadIdAndStatus(downloadEntity.id!!, Download.SLICE_STATUS_DOWNLOADING)!! < 3) {
                    //小于3就继续添加下载任务
                    //将切片状态设置为下载中
                    item.status = Download.SLICE_STATUS_DOWNLOADING
                    //更新下载状态
                    downloadSliceDao?.updateItem(item)
                    //发起下载
                    downloadSlice(item, downloadEntity);
                }
            }
        } else {
            //没有未完成下载
            //判断下载中的任务是否为0
            if (downloadSliceDao?.getListSizeForDownloadIdAndStatus(downloadEntity.id!!, Download.SLICE_STATUS_DOWNLOADING)!! == 0) {
                //设置状态在合并中
                downloadEntity.status = Download.STATUS_MERGING
                //更新下载状态
                downloadDao?.updateItem(downloadEntity)
                //获取碎片列表
                val downloadSliceEntityCompleteList = downloadSliceDao?.getListForDownloadId(downloadEntity.id!!)
                //判断列表是否为空
                if (downloadSliceEntityCompleteList != null) {
                    Logger.i("${downloadEntity}")
                    //获取设置的下载目录
                    var file = File(downloadEntity.dirPath)
                    //创建下载目录
                    file.mkdirs()
                    //设置下载文件名
                    file = File(file, downloadEntity.fileName)
                    //获取文件输出流,并打开追加模式
                    val fileOutputStream = FileOutputStream(file, true)
                    //便利切片
                    downloadSliceEntityCompleteList.forEach { item ->
                        //获取切片文件
                        val sliceFile = File(item.filePath)
                        //把切片的byte数组写入文件输出流中
                        fileOutputStream.write(sliceFile.readBytes())
                        //删除切片文件
                        sliceFile.delete()
                    }
                    //关闭文件输出流
                    fileOutputStream.close()
                    //删除合并后的切片
                    downloadSliceDao?.deleteAllItem(downloadSliceEntityCompleteList)
                    //将下载任务状态修改为下载成功
                    downloadEntity.status = Download.STATUS_COMPLETE
                    //更新数据库下载任务数据
                    downloadDao?.updateItem(downloadEntity)
                    //下载完毕,更新下载任务
                    updateDownloadTask()
                }
            }
        }
    }

    //更新下载任务
    fun updateDownloadTask() {
        //获取可自动开始的未完成下载列表
        val downloadEntityList = downloadDao?.getUndoneDownloadList()
        //判断列表是否为空
        if (downloadEntityList != null) {
            //遍历下载列表
            for (item in downloadEntityList) {
                //判断正在执行的任务数是否到达3个
                if (downloadDao?.getDownloadForStatus(Download.STATUS_DOWNLOADING)!! < 3) {
                    //没有到达就添加新的下载
                    //将状态设置为下载中
                    item.status = Download.STATUS_DOWNLOADING
                    //更新状态
                    downloadDao?.updateItem(item)
                    //开始下载
                    startDownload(item)
                } else {
                    //已经到了就跳出循环
                    break
                }
            }
        }
    }

    //服务即将关闭前的回调
    override fun onDestroy() {
        Log.i("服务", "关闭")
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}

