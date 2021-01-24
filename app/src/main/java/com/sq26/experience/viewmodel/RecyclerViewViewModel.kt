package com.sq26.experience.viewmodel

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sq26.experience.data.RecyclerViewDao
import com.sq26.experience.data.RecyclerViewItem
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
        viewModelScope.launch(Dispatchers.IO){
            recyclerViewRepository.insert(RecyclerViewItem())
        }
    }

    fun getQueryAll() = recyclerViewRepository.queryAll().asLiveData()
}

@Singleton
class RecyclerViewRepository @Inject constructor(private val recyclerViewDao: RecyclerViewDao) {
    fun insert(recyclerViewItem: RecyclerViewItem) {
        recyclerViewDao.insert(recyclerViewItem)
    }

    fun queryAll() = recyclerViewDao.queryAll()
}