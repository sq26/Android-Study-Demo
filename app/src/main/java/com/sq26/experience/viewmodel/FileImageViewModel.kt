package com.sq26.experience.viewmodel

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.*
import com.sq26.experience.ui.activity.file.ImageInfo
import com.sq26.experience.ui.activity.file.ImageType
import com.sq26.experience.util.FileUtil
import com.sq26.experience.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class FileImageViewModel : ViewModel() {

    init {
        Log.i(this,"viewmodel")
    }

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
                        val bucketId =
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                            } else {
                                0
                            }
                        val bucketDisplayName =
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                            } else {
                                FileUtil.getParentFileName(
                                    cursor.getString(
                                        cursor.getColumnIndex(
                                            MediaStore.Images.Media.DATA
                                        )
                                    )
                                )
                            }
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
                            ContentUris.withAppendedId(uri, id),
                            bucketId,
                            bucketDisplayName
                        )
                        if (!list.contains(item))
                            list.add(item)
                    }
                }

                val imagesTypeList = mutableListOf<ImageType>()
                list.forEach { item ->
                    val index = imagesTypeList.indexOfFirst {
                        it.title == item.bucketDisplayName
                    }
                    if (index == -1) {
                        //不存在,需要创建
                        imagesTypeList.add(
                            ImageType(
                                item.bucketDisplayName,
                                item.uri,
                                mutableListOf(item)
                            )
                        )
                    } else {
                        //已存在加数据
                        imagesTypeList[index].list.add(item)
                    }
                }
                Log.i(imagesTypeList.toString())
                imagesTypeLiveData.postValue(imagesTypeList)

            }
        }
    }

    fun imageListFlow(index: Int) = flow<List<ImageInfo>> {
        imagesTypeLiveData.value?.let {
            emit(it[index].list)
        }
    }.flowOn(Dispatchers.Default).asLiveData()
}