package com.sq26.experience.util.network.download

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Environment
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.*
import com.sq26.experience.aidl.IDownloadServiceCallbackInterface
import com.sq26.experience.aidl.IDownloadServiceInterface
import com.sq26.experience.app.MyApp
import com.sq26.experience.util.FileUtil
import com.sq26.experience.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.Request
import okio.utf8Size
import java.io.File
import java.lang.Exception
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.math.log


/**
 * context:上下文
 * url:下载链接
 */
class Download {
    //创建伴生对象
    //语法companion object [伴生对象的名称(不写默认和类名相同)]

    companion object {
        const val STATUS_CREATE = 0   //任务创建(还未切片前)
        const val STATUS_WAIT = 1   //等待开始下载(切片完成)
        const val STATUS_DOWNLOADING = 2   //下载中(切片完成)
        const val STATUS_AUTO_PAUSE = 3   //自动暂停
        const val STATUS_PAUSE = 4   //主动暂停
        const val STATUS_MERGING = 5   //合并中
        const val STATUS_COMPLETE = 6   //下载完成
        const val STATUS_ERROR = 7   //下载失败

        const val SLICE_STATUS_COMPLETE = 1   //切片下载完成
        const val SLICE_STATUS_NOT_FINISHED = 2   //切片下载未完成
        const val SLICE_STATUS_DOWNLOADING = 3   //切片下载中
        //下载目录,可在myapp中修改默认下载地址
        var DefaultDownloadPath =
            MyApp.app.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath

        //重试下载任务
        fun retries(id: Int) {
            //声明下载服务视图
            val intent = Intent(MyApp.app, DownloadService::class.java)
            //设置下载id
            intent.putExtra("id", id)
            //设置下载类型
            intent.putExtra("type", "add")
            //启动下载服务服务（如果这个服务已经启动，不会打开两次）
            MyApp.app.startService(intent)
        }

        //添加下载任务,返回下载任务id
        fun add(
            url: String,
            fileName: String = FileUtil.getFileName(url),
            dirPath: String = DefaultDownloadPath,
            fileUri: String = ""
        ): Int {
            //声明新文件名
            var newFileName = fileName
            //申明documentFile
            val documentFile: DocumentFile
            if (fileUri.isEmpty()) {
                //创建目录
                File(dirPath).mkdirs()
                //申明文件
                var file = File(dirPath, newFileName)
                //检擦文件是否存在
                if (file.exists()) {
                    //存在,在文件名后面加序号
                    //申明文件是否存在
                    var isAvailable = false
                    //申明序号1
                    var i = 1
                    //把文件名拆分出名称和格式名
                    val newFileNameArray = newFileName.split(".")
                    //获取名称
                    val prefix = newFileNameArray[0]
                    //获取后缀文件名
                    val suffix =
                        if (newFileNameArray.size > 1) newFileNameArray[newFileNameArray.size - 1] else ""
                    //判断是否存在
                    while (!isAvailable) {
                        //不存在继续循环
                        //在文件名和后缀之间加序号
                        file = File(dirPath, "$prefix($i).$suffix")
                        //加擦是否存在
                        if (file.exists()) {
                            //存在,序号加1,循环继续
                            i++
                        } else {
                            //文件不存在
                            //表示文件存在
                            isAvailable = true
                            //获取新文件名
                            newFileName = file.name
                            //创建此文件
                            file.createNewFile()
                        }
                    }
                } else {
                    //创建此文件
                    file.createNewFile()
                }
                documentFile = DocumentFile.fromFile(file)
            } else {
                documentFile = DocumentFile.fromSingleUri(MyApp.app, Uri.parse(fileUri))!!
            }
            //创建下载信息
            var downloadEntity =
                DownloadEntity(
                    url = url,
                    fileUri = documentFile.uri.toString(),
                    fileName = newFileName
                )
            //获取下载的Dao层
            val downloadDao = DownloadDatabase.getInstance(MyApp.app).getDownloadDao()
            //创建新的下载记录
            downloadDao.addItem(downloadEntity)
            //获取新创建的下载记录获取其中的id
            downloadEntity =
                downloadDao.getDownloadForUrlAndFileNameAndDirPath(newFileName, downloadEntity.fileUri)
            //声明下载服务视图
            val intent = Intent(MyApp.app, DownloadService::class.java)
            //设置下载id
            intent.putExtra("id", downloadEntity.id!!)
            //设置下载类型
            intent.putExtra("type", "add")
            //启动下载服务服务（如果这个服务已经启动，不会打开两次）
            MyApp.app.startService(intent)
            //返回下载id
            return downloadEntity.id!!
        }

        //监听下载进度
        suspend fun progress(id: Int, callback: (Progress) -> Unit) {
            withContext(Dispatchers.IO) {
                //获取下载的Dao层
                val downloadDao = DownloadDatabase.getInstance(MyApp.app).getDownloadDao()
                //获取下载切片的Dao层
                val downloadSliceDao = DownloadDatabase.getInstance(MyApp.app).getDownloadSliceDao()
                var downloadEntity: DownloadEntity
                var tolal: Long
                while (true) {
                    //获取下载信息
                    downloadEntity = downloadDao.getDownloadForId(id)
                    Log.i(downloadEntity.status.toString())
                    when (downloadEntity.status) {
                        STATUS_DOWNLOADING -> {
                            //申明已加载总大小
                            tolal = downloadSliceDao.getAlreadySizeTotal(id)
                            //抛出当前进度
                            callback(Progress(tolal, downloadEntity.size))
                        }
                        STATUS_COMPLETE, STATUS_ERROR -> {
                            callback(Progress(downloadEntity.size, downloadEntity.size))
                            //当下载完毕时跳出循环
                            break
                        }
                        else -> {
                            callback(Progress(0, downloadEntity.size))
                        }
                    }
                    //每500毫秒查询一次,避免过度消耗资源
                    delay(300)
                }
            }
        }

        //暂停任务
        fun pause(id: Int) {
            thread {
                val downloadDao = DownloadDatabase.getInstance(MyApp.app).getDownloadDao()
                val item = downloadDao.getDownloadForId(id)
                item.status = STATUS_PAUSE
                downloadDao.updateItem(item)
            }
        }
    }

    fun delete() {
        val intent = Intent(MyApp.app, DownloadService::class.java)
        //启动下载服务服务（如果这个服务已经启动，不会打开两次）
        MyApp.app.startService(intent)
    }
}

data class Progress(var current: Long, var all: Long)

