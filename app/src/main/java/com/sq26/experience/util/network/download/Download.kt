package com.sq26.experience.util.network.download

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.asLiveData
import com.sq26.experience.app.MyApp
import com.sq26.experience.data.DownloadDao
import com.sq26.experience.util.FileUtil
import com.sq26.experience.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.thread

object DownloadStatus {
    //任务创建(还没分配储存空间)
    const val CREATE = 0

    //等待开始下载(分配好储存进入等待)
    const val WAIT = 1

    //下载中
    const val DOWNLOADING = 2

    //自动暂停
    const val AUTO_PAUSE = 3

    //主动暂停
    const val PAUSE = 4

    //下载完成
    const val COMPLETE = 5

    //下载失败
    const val ERROR = 6

    //初始化
    const val INIT = 7
}

object DownloadErrorType {
    //没有错误
    const val NO = 0

    //初始化异常
    const val INIT = 1

    //下载中
    const val DOWNLOADING = 2

    //自动暂停
    const val AUTO_PAUSE = 3

    //主动暂停
    const val PAUSE = 4

    //下载完成
    const val COMPLETE = 5

    //下载失败
    const val ERROR = 6
}

/**
 * context:上下文
 * url:下载链接
 */
@Singleton
class Download @Inject constructor(
    private val downloadDao: DownloadDao,
    @ApplicationContext private val context: Context
) {

    init {
        Log.i("下载服务跟随应用生命周期,在调用页面中启动")
        update()
    }

    //更新下载任务
    private fun update() {
        //声明下载服务视图
        val intent = Intent(context, DownloadService::class.java)
        intent.putExtra("type", "update")
        //启动下载服务服务（如果这个服务已经启动，不会打开两次）
        context.startService(intent)
    }

    //flwo在没有观察者后会自动取消
    fun getItem(id: Long) = flow {
        var item = downloadDao.getDownloadForId(id)
        emit(item)
        while (item.status != DownloadStatus.COMPLETE && item.status != DownloadStatus.ERROR) {
            item = downloadDao.getDownloadForId(id)
            //需要通过阻塞函数来检查是否需要取消协程
            delay(if (item.status == DownloadStatus.WAIT || item.status == DownloadStatus.PAUSE) 1000 else 300)
            emit(item)
            Log.i("循环getItem")
        }
        emit(item)
    }.flowOn(Dispatchers.IO).asLiveData()

    suspend fun add(
        url: String,
        uri: Uri
    ) {
        return withContext(Dispatchers.IO) {
            //申明documentFile
            val documentFile = DocumentFile.fromSingleUri(context, uri)!!
            //创建下载信息
            var downloadEntity = com.sq26.experience.data.DownloadEntity(
                url = url,
                fileUri = uri.toString(),
                fileName = documentFile.name ?: ""
            )
            //创建新的下载记录
            downloadDao.addItem(downloadEntity)
            //获取新创建的下载记录获取其中的id
            downloadEntity =
                downloadDao.getDownloadForUrlAndFileNameAndDirPath(
                    documentFile.name ?: "",
                    downloadEntity.fileUri
                )
            //声明下载服务视图
            val intent = Intent(context, DownloadService::class.java)
            //设置下载id
            intent.putExtra("id", downloadEntity.id)
            //设置下载类型
            intent.putExtra("type", "add")
            //启动下载服务服务（如果这个服务已经启动，不会打开两次）
            context.startService(intent)
            //返回下载id
            downloadEntity.id
        }
    }

    suspend fun add(
        url: String,
        fileName: String = FileUtil.getFileName(url),
        filePath: String = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath
    ): Long {
        return withContext(Dispatchers.IO) {
            //声明新文件名
            var newFileName = fileName
            //创建目录
            File(filePath).mkdirs()
            //申明文件
            var file = File(filePath, newFileName)
            //检擦文件是否存在
            if (file.exists()) {
                //存在,在文件名后面加序号
                //申明文件是否存在
                var isAvailable = true
                //申明序号1
                var i = 1
                //把文件名拆分出名称和格式名
                val newFileNameArray = newFileName.split(".")
                //获取后缀文件名
                val suffix =
                    if (newFileNameArray.size > 1) newFileNameArray[newFileNameArray.size - 1] else ""
                //获取名称
                val prefix =
                    if (suffix.isEmpty()) newFileName else newFileName.replace(".$suffix", "")
                //判断是否存在
                while (isAvailable) {
                    //存在继续循环
                    //在文件名和后缀之间加序号
                    file = File(filePath, "$prefix($i)${if (suffix.isEmpty()) "" else ".$suffix"}")
                    //加擦是否存在
                    if (file.exists()) {
                        //存在,序号加1,循环继续
                        i++
                    } else {
                        //文件不存在
                        isAvailable = false
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
            //申明documentFile
            val documentFile = DocumentFile.fromFile(file)
            //创建下载信息
            var downloadEntity = com.sq26.experience.data.DownloadEntity(
                url = url,
                fileUri = documentFile.uri.toString(),
                fileName = newFileName
            )
            //创建新的下载记录
            downloadDao.addItem(downloadEntity)
            //获取新创建的下载记录获取其中的id
            downloadEntity =
                downloadDao.getDownloadForUrlAndFileNameAndDirPath(
                    newFileName,
                    downloadEntity.fileUri
                )
            //声明下载服务视图
            val intent = Intent(context, DownloadService::class.java)
            //设置下载id
            intent.putExtra("id", downloadEntity.id)
            //设置下载类型
            intent.putExtra("type", "add")
            //启动下载服务服务（如果这个服务已经启动，不会打开两次）
            context.startService(intent)
            //返回下载id
            downloadEntity.id
        }
    }


    fun queryAllFlow() = downloadDao.queryAllList().asLiveData()

    fun deleteAll() = downloadDao.deleteAll()

    //暂停任务
     fun pause(id: Long) {
        thread {
            val item = downloadDao.getDownloadForId(id)
            item.status = DownloadStatus.PAUSE
            downloadDao.updateItem(item)
        }
    }

    //继续任务
    fun continuance(id: Long) {
        thread {
            val item = downloadDao.getDownloadForId(id)
            item.status = DownloadStatus.WAIT
            downloadDao.updateItem(item)
            update()
        }
    }

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

