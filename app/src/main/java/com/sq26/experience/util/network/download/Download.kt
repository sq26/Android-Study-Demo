package com.sq26.experience.util.network.download

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.asLiveData
import com.sq26.experience.data.DownloadDao
import com.sq26.experience.data.DownloadEntity
import com.sq26.experience.data.DownloadEntityDelete
import com.sq26.experience.data.DownloadEntityStatus
import com.sq26.experience.util.FileUtil
import com.sq26.experience.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.thread

object DownloadStatus {
    //任务创建(还没分配储存空间)
    const val CREATE = 0

    //等待初始化
    const val INIT = 7

    //初始化中
    const val INIT_ING = 8

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
        //启动下载服务服务（如果这个服务已经启动，不会打开两次,但可以更新）
        context.startService(Intent(context, DownloadService::class.java))
    }

    //flow在没有观察者后会自动取消
    fun getDownloadFlow(id: Long) = flow {
        var downloadEntity = downloadDao.getDownloadForIdAndNull(id)
        downloadEntity?.let {
            emit(it)
            while (it.status != DownloadStatus.COMPLETE && it.status != DownloadStatus.ERROR && it.status != DownloadStatus.PAUSE) {
                downloadEntity = downloadDao.getDownloadForIdAndNull(id)
                downloadEntity?.let { item ->
                    //需要通过阻塞函数来检查是否需要取消协程
                    delay(if (item.status == DownloadStatus.WAIT || item.status == DownloadStatus.PAUSE) 1000 else 300)
                    emit(item)
                }
            }
            downloadEntity?.let { item ->
                emit(item)
            }
        }
    }.flowOn(Dispatchers.IO).asLiveData()

    suspend fun add(
        url: String,
        uri: Uri
    ) {
        return withContext(Dispatchers.IO) {
            //申明documentFile
            val documentFile = DocumentFile.fromSingleUri(context, uri)!!
            //创建下载信息
            var downloadEntity = DownloadEntity(
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
            //更新任务
            update()
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
            var downloadEntity = DownloadEntity(
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
            //更新任务
            update()
            //返回下载id
            downloadEntity.id
        }
    }

    //获取下载列表的监听
    fun queryAllFlow() = downloadDao.queryAllList().asLiveData()

    fun deleteAll() = downloadDao.deleteAll()

    //暂停任务
    fun pause(id: Long) {
        thread {
            thread {
                downloadDao.updateStatus(DownloadEntityStatus(id, DownloadStatus.PAUSE))
            }
        }
    }

    //继续任务,重试任务
    fun continuance(id: Long) {
        thread {
            downloadDao.updateStatus(DownloadEntityStatus(id, DownloadStatus.INIT))
            //更新任务
            update()
        }
    }

    //删除任务,isKeepFile是否保留文件
    fun delete(id: Long, isKeepFile: Boolean = true) {
        thread {
            val downloadEntity = downloadDao.getDownloadForId(id)
            if (downloadEntity.status == DownloadStatus.COMPLETE || downloadEntity.status == DownloadStatus.ERROR) {
                //判断是否保留文件
                if (!downloadEntity.isKeepFile) {
                    //删除文件
                    DocumentFile.fromSingleUri(context, Uri.parse(downloadEntity.fileUri))
                        ?.delete()
                }
                //删除记录
                downloadDao.deleteForItem(downloadEntity)
            } else {
                downloadDao.updateDelete(DownloadEntityDelete(id, isKeepFile = isKeepFile))
                downloadDao.updateStatus(DownloadEntityStatus(id, DownloadStatus.PAUSE))
                var isDelete = false
                while (!isDelete) {
                    Thread.sleep(100)
                    downloadDao.getDownloadForIdAndNull(id)?.let {
                        if (it.isDelete) {
                            isDelete = true
                            downloadDao.deleteForItem(it)
                        }
                    }
                }
            }
        }
    }
}


