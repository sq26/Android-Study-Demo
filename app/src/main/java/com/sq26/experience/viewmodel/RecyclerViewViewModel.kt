package com.sq26.experience.viewmodel

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sq26.experience.data.RecyclerViewDao
import com.sq26.experience.data.RecyclerViewItem
import com.sq26.experience.util.Log
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

class RecyclerViewViewModel @ViewModelInject constructor(
    private val recyclerViewRepository: RecyclerViewRepository,
    @ActivityContext private val context: Context
) : ViewModel() {

    fun insert() {
        viewModelScope.launch(Dispatchers.IO) {
            //排序字段取最大值加一
            recyclerViewRepository.insert(RecyclerViewItem(sort = recyclerViewRepository.getMaxSort() + 1))
        }
    }

    fun updateAll(list: List<RecyclerViewItem>){
        viewModelScope.launch(Dispatchers.IO) {
            recyclerViewRepository.updateAll(list)
        }
    }

    fun delete(item: RecyclerViewItem?) {
        if (item != null)
            viewModelScope.launch(Dispatchers.IO) {
                recyclerViewRepository.delete(item)
            }
    }

    fun getQueryAll() = recyclerViewRepository.queryAll().asLiveData()
}

@Singleton
class RecyclerViewRepository @Inject constructor(private val recyclerViewDao: RecyclerViewDao) {
    fun insert(recyclerViewItem: RecyclerViewItem) = recyclerViewDao.insert(recyclerViewItem)

    fun delete(recyclerViewItem: RecyclerViewItem) = recyclerViewDao.delete(recyclerViewItem)

    fun queryAll() = recyclerViewDao.queryAll()

    fun getMaxSort() = recyclerViewDao.getMaxSort()

    fun updateAll(list: List<RecyclerViewItem>) = recyclerViewDao.updateAll(list)
}