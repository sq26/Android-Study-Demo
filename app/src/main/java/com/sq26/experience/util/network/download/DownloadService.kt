package com.sq26.experience.util.network.download

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import com.sq26.experience.app.OkHttpUtil
import com.sq26.experience.data.DownloadDao
import com.sq26.experience.data.DownloadEntity
import com.sq26.experience.util.Log
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.*
import java.io.IOException
import java.util.concurrent.*
import javax.inject.Inject
import kotlin.concurrent.thread

@AndroidEntryPoint
class DownloadService : Service() {
    //声明下载表的操作类
    @Inject
    lateinit var downloadDao: DownloadDao

    //服务第一次启动时会调用这个方法
    override fun onCreate() {
        Log.i("启动", "服务")
//        Debug.waitForDebugger()
        super.onCreate()
    }

    //用startService方法启动服务会进入这个方法
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        thread {
            intent?.let {
                when (it.getStringExtra("type")) {
                    "add" -> {
                        //获取下载信息
                        val downloadEntity =
                            downloadDao.getDownloadForId(intent.getLongExtra("id", 0))
                        Log.i("status:${downloadEntity.status}")
                        initDownload(downloadEntity)
                        //获取下载状态
//                        when (downloadEntity.status) {
//                            DownloadStatus.CREATE -> {
//                                initDownload(downloadEntity)
//                            }
//                            DownloadStatus.WAIT, DownloadStatus.AUTO_PAUSE -> {
//                                updateDownloadTask()
//                            }
//                        }
                    }
                    "update" -> {
                        //初始化下载任务
                        updateInitDownloadTask()
                        //检擦下载任务
                        updateDownloadTask()
                    }
                }
            }
//            if (intent != null) {
//                if (intent.getStringExtra("type") == "add") {


//                when (downloadEntity.status) {
//                    //刚创建,维修完,错误是需要重新切片的,下载完的切片任务也被删除了
//                    Download.STATUS_CREATE,
//                    Download.STATUS_COMPLETE,
//                    Download.STATUS_ERROR -> {
//                        //去切片
//                        compareAndSlice(downloadEntity)
//                    }
//                    //等待状态,自动暂停
//                    Download.STATUS_WAIT,
//                    Download.STATUS_AUTO_PAUSE -> {
//                        //更新下载任务
//                        updateDownloadTask()
//                    }
//                    //暂停
//                    Download.STATUS_PAUSE -> {
//                        //将状态改为准备下载状态
//                        downloadEntity.status = Download.STATUS_WAIT
//                        //更新状态
//                        downloadDao.updateItem(downloadEntity)
//                        //更新下载任务
//                        updateDownloadTask()
//                    }
//                }

//                }else if ()
//
//            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    //获取剩余可用内存
    private fun totalFree(): Long {
        val runtime = Runtime.getRuntime()
        //剩余可用内存 = 最大可使用内存-已使用内存(全部已使用内存-可回收内存)
        return runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory())
    }

    //1MB内存
    private val mb1 = 1024 * 1024

    private fun initDownload(downloadEntity: DownloadEntity) {
        //获取http客户端
        //获取一个新的通信
        OkHttpUtil.getInstance().newCall(
            //使用head请求,只获取请求头
            Request.Builder()
                .head()
                .url(downloadEntity.url)
                .build()
            //发起请求通信并设置异步回调
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //判断失败次数
                if (downloadEntity.errorFrequency < 3) {
                    //记录下载失败次数
                    downloadEntity.errorFrequency = downloadEntity.errorFrequency + 1
                    //更新下载详情
                    downloadDao.updateItem(downloadEntity)
                    //重试
                    initDownload(downloadEntity)
                } else {
                    //修改下载状态为失败
                    downloadEntity.status = DownloadStatus.ERROR
                    //更新下载详情
                    downloadDao.updateItem(downloadEntity)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                //Content-Range用于响应头中，在发出带 Range 的请求后，服务器会在 Content-Range 头部返回当前接受的范围和文件总大小。
                //Content-Range: bytes 0-499/22400
                //判断请求是否成功
                if (response.code == 200 || response.code == 206) {
                    //获取要下载的文件大小
                    val size = response.header("Content-Length", "0")?.toLong()!!
                    //判断要下载的文件是否已变化
                    //通过判断是否第一次下载
                    if (downloadEntity.status != Download.STATUS_CREATE) {
//                        //判断保存的大小和现在的大小是否一致
                        if (size != downloadEntity.size) {

                        } else {


                        }
//                        //不一致,文件已改变需要清除之前的切片数据
//                            downloadSliceDao.deleteAllItem(
//                                downloadSliceDao.getListForDownloadId(downloadEntity.id!!)
//                            )
//                        //判断有没有切片数据
//                        if (downloadSliceDao.getListForDownloadId(downloadEntity.id!!).size > 0) {
//                            //有切片数据不用重写切片
//                            //状态改为等待下载
//                            downloadEntity.status = Download.STATUS_WAIT
//                            //刷新状态
//                            downloadDao.updateItem(downloadEntity)
//                            //更新下载任务
//                            updateDownloadTask()
//                            return
//                        }
                    }
                    //设置文件大小
                    downloadEntity.size = size

                    if (totalFree() < mb1) {
                        Log.i("内存不足")
                        return
                    }

                    val outputStream =
                        contentResolver.openOutputStream(Uri.parse(downloadEntity.fileUri))

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

                    //数据填充完成,把下载状态修改为等待下载
                    downloadEntity.status =
                        if (downloadDao.getDownloadForId(downloadEntity.id).status == DownloadStatus.PAUSE) DownloadStatus.PAUSE else DownloadStatus.WAIT
                    //更新下载信息
                    downloadDao.updateItem(downloadEntity)
                    Log.i("更新了")
                    //更新初始化下载任务
                    updateInitDownloadTask()
                    //更新下载任务
                    updateDownloadTask()
                } else {
                    //判断失败次数
                    if (downloadEntity.errorFrequency < 3) {
                        //记录下载失败次数
                        downloadEntity.errorFrequency = downloadEntity.errorFrequency + 1
                        //更新下载详情
                        downloadDao.updateItem(downloadEntity)
                        //重试
                        initDownload(downloadEntity)
                    } else {
                        //修改下载状态为失败
                        downloadEntity.status = DownloadStatus.ERROR
                        //更新下载详情
                        downloadDao.updateItem(downloadEntity)
                        //更新初始化下载任务
                        updateInitDownloadTask()
                        //更新下载任务
                        updateDownloadTask()
                    }
                }
            }
        })
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
            if (downloadDao.getDownloadForStatus(DownloadStatus.DOWNLOADING) < 3) {
                //没有到达就添加新的下载
                //将状态设置为下载中
                item.status = DownloadStatus.DOWNLOADING
                //更新状态
                Log.i("更新了")
                downloadDao.updateItem(item)
                //开始下载任务
                Log.i("开始下载任务")
                thread {
                    startDownload(item)
                }
            } else {
                //已经到了就结束调用
                return
            }
        }
    }

    //更新初始化任务
    private fun updateInitDownloadTask() {
        Log.i("更新下载任务")
        //获取可自动开始的未完成下载列表
        val downloadEntityList = downloadDao.getUndoneInitDownloadList()
        Log.i("下载任务$downloadEntityList")
        //遍历可下载的任务
        for (item in downloadEntityList) {
            //判断正在执行的任务数是否到达3个
            if (downloadDao.getDownloadForStatus(DownloadStatus.INIT) < 1) {
                //没有到达就添加新的下载
                //将状态设置为下载中
                item.status = DownloadStatus.INIT
                //更新状态
                Log.i("更新了")
                downloadDao.updateItem(item)
                //开始下载任务
                Log.i("开始下载任务")
                thread {
                    initDownload(item)
                }
            } else {
                //已经到了就结束调用
                return
            }
        }
    }

    private fun startDownload(downloadEntity: DownloadEntity) {
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
            if (totalFree() > mb1 * 2) {
                val outputStream =
                    contentResolver.openOutputStream(Uri.parse(downloadEntity.fileUri))
                if (downloadEntity.alreadySize > 0) {
                    val byteArray = ByteArray(mb1)
                    val inputStream =
                        contentResolver.openInputStream(Uri.parse(downloadEntity.fileUri))
                    var i = 0
                    while (i < downloadEntity.alreadySize) {
                        inputStream?.read(byteArray)
                        val count: Int =
                            if (mb1 < (downloadEntity.alreadySize - i)) mb1 else (downloadEntity.alreadySize - i).toInt()
                        outputStream?.write(byteArray, 0, count)
                        i += count
                    }
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
                        Log.i("socket异常,手动调用取消请求引起")
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
                        //设置已下载大小
                        downloadEntity.alreadySize = sum
                        //重置失败次数
                        downloadEntity.errorFrequency = 0
                        //重置失败次数
                        downloadEntity.status =
                            downloadDao.getDownloadForId(downloadEntity.id).status
                        //更新数据库切片数据
                        Log.i("更新了")
                        downloadDao.updateItem(downloadEntity)
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
                if (downloadDao.getDownloadForId(downloadEntity.id).status == DownloadStatus.PAUSE) {
                    //更新下载任务,开始下载另一个任务
                    updateDownloadTask()
                } else if (len == -1 && downloadEntity.alreadySize != downloadEntity.size) {
                    //判断失败次数
                    if (downloadEntity.errorFrequency < 3) {
                        //记录下载失败次数
                        downloadEntity.errorFrequency = downloadEntity.errorFrequency + 1
                        //更新下载详情
                        downloadDao.updateItem(downloadEntity)
                        //重试
                        startDownload(downloadEntity)
                    } else {
                        //修改下载状态为失败
                        downloadEntity.status = DownloadStatus.ERROR
                        //更新下载详情
                        downloadDao.updateItem(downloadEntity)
                        //更新下载任务,开始下载另一个任务
                        updateDownloadTask()
                    }
                } else {
                    //下载完毕
                    downloadEntity.status = DownloadStatus.COMPLETE
                    //更新数据库切片数据
                    Log.i("更新了")
                    downloadDao.updateItem(downloadEntity)
                    //更新下载任务,开始下载另一个任务
                    updateDownloadTask()
                }
            } else {
                Log.i("剩余内存不足")
            }
        } else {
            //判断失败次数
            if (downloadEntity.errorFrequency < 3) {
                //记录下载失败次数
                downloadEntity.errorFrequency = downloadEntity.errorFrequency + 1
                //更新下载详情
                downloadDao.updateItem(downloadEntity)
                //重试
                startDownload(downloadEntity)
            } else {
                //修改下载状态为失败
                downloadEntity.status = DownloadStatus.ERROR
                //更新下载详情
                downloadDao.updateItem(downloadEntity)
                //更新下载任务,开始下载另一个任务
                updateDownloadTask()
            }
        }

    }

//
//    //下载切片
//    private fun downloadSlice(
//        downloadSliceEntity: DownloadSliceEntity,
//        downloadEntity: DownloadEntity
//    ) {
//        Log.i("切片下载开始:$downloadSliceEntity")
//        //创建一个请求
//        //Range用于请求头中，指定第一个字节的位置和最后一个字节的位置，一般格式：
//        //Range: bytes=0-499 表示第 0-499 字节范围的内容
//        //Range: bytes=500-999 表示第 500-999 字节范围的内容
//        //Range: bytes=-500 表示最后 500 字节的内容
//        //Range: bytes=500- 表示从第 500 字节开始到文件结束部分的内容
//        //Range: bytes=0-0,-1 表示第一个和最后一个字节
//        //Range: bytes=500-600,601-999 同时指定几个范围
//        val request = Request.Builder()
//            .url(downloadEntity.url)
//            //指定获取开始字节和结束字节
//            .addHeader(
//                "Range",
//                "bytes=${downloadSliceEntity.startIndex + downloadSliceEntity.alreadySize}-${downloadSliceEntity.endIndex}"
//            )
//            .build()
//        //获取通信
//        val call = OkHttpUtil.getInstance()
//            .newBuilder()
//            //连接超时时间20秒
//            .connectTimeout(20, TimeUnit.SECONDS)
//            //写入超时时间
//            .writeTimeout(20, TimeUnit.SECONDS)
//            //通讯超时时间20秒
//            .callTimeout(20, TimeUnit.SECONDS)
//            //读取超时30秒
//            .readTimeout(20, TimeUnit.SECONDS)
//            //设置不自动重试
//            .retryOnConnectionFailure(false)
//            .build()
//            .newCall(request)
//        //发起请求获取response
//        val response = call.execute()
//        if (response.code == 200 || response.code == 206) {
//            //判断有没有创建过文件路径
//            if (downloadSliceEntity.filePath.isEmpty())
//            //没创建过,创建文件路径
//                downloadSliceEntity.filePath = File(
//                    getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
//                    System.currentTimeMillis().toString() + downloadSliceEntity.id
//                ).absolutePath
//            //声明file
//            val file = File(downloadSliceEntity.filePath)
//            //判断文件是否存在
//            if (!file.exists())
//            //不存在就创建新文件
//                file.createNewFile()
//            //获取文件输出流
//            val fileOutputStream = FileOutputStream(file, true)
//            //获取response的输入流
//            val inputStream = response.body?.byteStream()
//            //申明字节数组
//            val bytes = ByteArray(8192)
//            //声明单次获取长度
//            var len = 0
//            //申明已下载大小
//            var sum = downloadSliceEntity.alreadySize
//            //单次获取大小不等与-1,循环继续
//            while (len != -1) {
//                //获取单次下载大小
//                len = try {
//                    inputStream?.read(bytes) ?: -1
//                } catch (e: SocketException) {
//                    //socket异常,手动调用取消请求引起
//                    Log.i("socket异常,手动调用取消请求引起")
//                    -1
//                } catch (e: SocketTimeoutException) {
//                    //读取超时导致
//                    Log.i("SocketTimeoutException读取超时导致")
//                    -1
//                } catch (e: InterruptedIOException) {
//                    //线程超时
//                    Log.i("InterruptedIOException线程超时")
//                    -1
//                }
//
//                //判断单次大小不等于-1时继续
//                if (len != -1) {
//                    //写入到文件输出流,bytes字节流,bytes开始位置,len是bytes结束位置,这里不能取完整的bytes,因为完整的bytes写死了4096的大小
//                    fileOutputStream.write(bytes, 0, len)
//                    //累加已下载大小
//                    sum += len
//                    //设置已下载大小
//                    downloadSliceEntity.alreadySize = sum
//                    //更新数据库切片数据
//                    downloadSliceDao.updateItem(downloadSliceEntity)
//                    //判断是否主动暂停
//                    if (downloadDao.getDownloadForId(downloadEntity.id!!).status == Download.STATUS_PAUSE) {
//                        //主动暂停就结束请求
//                        call.cancel()
//                    }
//                }
//            }
//            //把缓冲区的数据强行输出
//            fileOutputStream.flush()
//            //关闭文件输出流
//            fileOutputStream.close()
//            //关闭输入流
//            inputStream?.close()
//            Log.i("切片下载中:$downloadSliceEntity")
//            //判断切片是否下载完成
//            if (downloadSliceEntity.endIndex - downloadSliceEntity.startIndex + 1 == downloadSliceEntity.alreadySize) {
//                //设置状态为已完成
//                downloadSliceEntity.status = Download.SLICE_STATUS_COMPLETE
//                //更新数据库切片数据
//                downloadSliceDao.updateItem(downloadSliceEntity)
//                //检查并下载新的切片
//                startDownload(downloadEntity)
//            } else {
//                //没有完成就重试
//                downloadSlice(downloadSliceEntity, downloadEntity)
//            }
//        } else {
//            Log.i("切片下载失败:$downloadSliceEntity")
//            //连接失败
//            //将下载任务状态修改为下载失败
//            downloadEntity.status = Download.STATUS_ERROR
//            //更新数据库下载任务数据
//            downloadDao.updateItem(downloadEntity)
//            //将下载任务修改为未完成
//            downloadSliceEntity.status = Download.SLICE_STATUS_NOT_FINISHED
//            //更新数据库切片数据
//            downloadSliceDao.updateItem(downloadSliceEntity)
//            //更新下载任务,开始下载另一个任务
//            updateDownloadTask()
//        }
//    }
//
//    //启动下载
//    private fun startDownload(downloadEntity: DownloadEntity) {
//        //获取所有未完成下载
//        val downloadSliceEntityList = downloadSliceDao.getListForDownloadIdAndStatus(
//            downloadEntity.id!!,
//            Download.SLICE_STATUS_NOT_FINISHED
//        )
//
//        Log.i("获取所有未完成下载$downloadSliceEntityList")
//        //判断还有没有未下载切片
//        if (downloadSliceEntityList.size > 0) {
//            //遍历下载切片任务
//            for (item in downloadSliceEntityList) {
//                //判断当前下载的切片任务数量是否小于3
//                if (downloadSliceDao.getListSizeForDownloadIdAndStatus(
//                        downloadEntity.id!!,
//                        Download.SLICE_STATUS_DOWNLOADING
//                    ) < 3
//                ) {
//                    //小于3就继续添加下载任务
//                    //将切片状态设置为下载中
//                    item.status = Download.SLICE_STATUS_DOWNLOADING
//                    //更新下载状态
//                    downloadSliceDao.updateItem(item)
//                    //发起切片下载
//                    Log.i("发起切片下载")
//                    downloadThreadPool.execute {
//                        downloadSlice(item, downloadEntity)
//                    }
//                }
//            }
//        } else {
//            //添加入线程池
//            downloadThreadPool.submit {
//                //没有未完成下载
//                //判断下载中的任务是否为0
//                if (downloadSliceDao.getListSizeForDownloadIdAndStatus(
//                        downloadEntity.id!!,
//                        Download.SLICE_STATUS_DOWNLOADING
//                    ) == 0
//                ) {
//                    //设置状态在合并中
//                    downloadEntity.status = Download.STATUS_MERGING
//                    //更新下载状态
//                    downloadDao.updateItem(downloadEntity)
//                    //获取碎片列表
//                    val downloadSliceEntityCompleteList =
//                        downloadSliceDao.getListForDownloadId(downloadEntity.id!!)
//                    //获取文件输出流,并打开追加模式
//                    val fileOutputStream =
//                        contentResolver.openOutputStream(Uri.parse(downloadEntity.fileUri), "wa")
//                    //便利切片
//                    downloadSliceEntityCompleteList.forEach { item ->
//                        //获取切片文件
//                        val sliceFile = DocumentFile.fromFile(File(item.filePath))
//                        //打开切片的输入流
//                        val sliceFileInputStream = contentResolver.openInputStream(sliceFile.uri)
//                        //把切片的byte数组写入文件输出流中
//                        fileOutputStream?.write(sliceFileInputStream?.readBytes())
//                        //关闭切片的输入流
//                        sliceFileInputStream?.close()
//                        //删除切片文件
//                        sliceFile.delete()
//                    }
//                    fileOutputStream?.flush()
//                    //关闭文件输出流
//                    fileOutputStream?.close()
//                    //删除合并后的切片
//                    downloadSliceDao.deleteAllItem(downloadSliceEntityCompleteList)
//                    //将下载任务状态修改为下载成功
//                    downloadEntity.status = Download.STATUS_COMPLETE
//                    //更新数据库下载任务数据
//                    downloadDao.updateItem(downloadEntity)
//                    //下载完毕,更新下载任务
//                    updateDownloadTask()
//                }
//            }
//        }
//    }
//
//    //更新下载任务
//    fun updateDownloadTask() {
//        Log.i("更新下载任务")
//        //获取可自动开始的未完成下载列表
//        val downloadEntityList = downloadDao.getUndoneDownloadList()
//        Log.i("下载任务$downloadEntityList")
//        //遍历可下载的任务
//        for (item in downloadEntityList) {
//            //判断正在执行的任务数是否到达3个
//            if (downloadDao.getDownloadForStatus(Download.STATUS_DOWNLOADING) < 3) {
//                //没有到达就添加新的下载
//                //将状态设置为下载中
//                item.status = Download.STATUS_DOWNLOADING
//                //更新状态
//                downloadDao.updateItem(item)
//                //开始下载任务
//                Log.i("开始下载任务")
//                startDownload(item)
//            } else {
//                //已经到了就结束调用
//                return
//            }
//        }
//    }

    //服务即将关闭前的回调
    override fun onDestroy() {
        Log.i("服务", "关闭")
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}

