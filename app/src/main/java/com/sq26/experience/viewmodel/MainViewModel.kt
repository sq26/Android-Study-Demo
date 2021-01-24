package com.sq26.experience.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import javax.inject.Singleton

@Singleton
class MainViewModel : ViewModel() {
    val isInit = liveData {
       emit(false)
    }
}