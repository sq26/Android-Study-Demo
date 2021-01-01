package com.sq26.experience.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData

class MainViewModel : ViewModel() {
    val isInit = liveData {
       emit(false)
    }
}