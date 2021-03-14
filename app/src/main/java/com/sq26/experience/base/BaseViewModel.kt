package com.sq26.experience.base

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel

/**
 * ViewModel基础类,可以有效的配合数据绑定框架的双向绑定
 */
open class BaseViewModel : ViewModel(), Observable {
    private val callbacks = PropertyChangeRegistry()
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    fun notifyPropertyChanged(propertyId: Int) {
        callbacks.notifyChange(this, propertyId)
    }
}