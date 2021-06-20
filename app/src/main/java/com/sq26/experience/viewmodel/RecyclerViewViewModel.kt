package com.sq26.experience.viewmodel

import androidx.lifecycle.ViewModel
import com.sq26.experience.data.RecyclerViewItem

class RecyclerViewViewModel : ViewModel() {

    val list = mutableListOf<RecyclerViewItem>()

    init {
        for (i in 0 until 30) {
            list.add(RecyclerViewItem(i))
        }
    }

    fun insert():Int {
        val position = list.size
        list.add(RecyclerViewItem(position))
        return position
    }

    fun delete(position: Int) {
        list.removeAt(position)
    }

}
