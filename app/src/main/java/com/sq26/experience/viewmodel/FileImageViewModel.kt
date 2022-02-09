package com.sq26.experience.viewmodel

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.*
import com.sq26.experience.ui.activity.file.ImageInfo
import com.sq26.experience.ui.activity.file.ImageType
import com.sq26.experience.util.FileUtil
import com.sq26.experience.util.Log
import com.sq26.experience.util.i
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File

class FileImageViewModel : ViewModel() {

    val imagesTypeLiveData = MutableLiveData<List<ImageType>>()
    val imageListLiveData = MutableLiveData<List<ImageType>>()

    fun initData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val list: MutableList<ImageInfo> = arrayListOf()

            for (uri in arrayOf(
                //主储存数据
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                //内部储存数据
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )) {
                val query = context.contentResolver.query(uri, null, null, null, null)
                //use方法用于自动处理游标异常和自动关闭游标
                query?.use { cursor ->
                    while (cursor.moveToNext()) {
                        Log.i(
                            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)),
                            "data"
                        )
                        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                        val size =
                            cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
                        val displayName =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                        val mimeType =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))
                        val title =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE))
                        val dateAdded =
                            cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
                        val dateModified =
                            cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))
                        val width =
                            cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH))
                        val height =
                            cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT))
                        var bucketId = ""
                        var bucketDisplayName = ""
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            bucketId =
                                cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                                    .toString()
                            bucketDisplayName =
                                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                        } else {
                            val file = File(
                                cursor.getString(
                                    cursor.getColumnIndex(
                                        MediaStore.Images.Media.DATA
                                    )
                                )
                            ).parentFile
                            bucketId = file?.absolutePath.orEmpty()
                            bucketDisplayName = file?.name.orEmpty()
                        }
                        val itemUri = ContentUris.withAppendedId(uri, id)
                        val item = ImageInfo(
                            id,
                            size,
                            displayName,
                            mimeType,
                            title,
                            dateAdded,
                            dateModified,
                            width,
                            height,
                            itemUri,
                            bucketId,
                            bucketDisplayName
                        )
                        if (!list.contains(item))
                            list.add(item)
                    }
                }

                val imagesTypeList = mutableListOf<ImageType>()

                list.map { it.bucketId }.toSet().forEach { bucketId ->
                    val list = list.filter { it.bucketId == bucketId }
                    imagesTypeList.add(
                        ImageType(
                            list[0].bucketDisplayName,
                            list[0].uri,
                            list
                        )
                    )
                }

                Log.i(imagesTypeList.toString())
                imagesTypeLiveData.postValue(imagesTypeList)

            }
        }
    }

    fun imageListFlow(index: Int) = flow {
        imagesTypeLiveData.value?.let {
            emit(it[index].list)
        }
    }.flowOn(Dispatchers.Default).asLiveData()


}