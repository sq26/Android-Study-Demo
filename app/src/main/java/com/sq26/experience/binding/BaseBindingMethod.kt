package com.sq26.experience.binding

import android.view.View
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import com.google.android.material.checkbox.MaterialCheckBox
import com.sq26.experience.util.Log

//指定自定义方法名称
//@BindingMethods(
//    value = [
//        BindingMethod(
//            type = View::class,
//            attribute = "android:vis",
//            method = "setVis"
//        )
//    ]
//)

object BaseBindingMethod {
    //自定义控件的属性和实现方法
    //让visibility属性可以接收Boolean类型数据
    @BindingAdapter("android:vis")
    //需要加JvmStatic才能创建使用java的静态对象
    @JvmStatic
    fun setVis(view: View, b: Boolean) {
        view.isVisible = b
        Log.i("自定义属性方法")
    }

    //让MaterialCheckBox属性可以绑定开关监听
    @BindingAdapter("android:onCheckedChange")
    //需要加JvmStatic才能创建使用java的静态对象
    @JvmStatic
    fun setOnCheckedChange(view: MaterialCheckBox, listener: CompoundButton.OnCheckedChangeListener) {
        view.setOnCheckedChangeListener(listener)
    }

    //自定义类型转换
    //当接收属性需要Int类型,输入的却是boolean类型时,自动转换类型,感觉这种方法容易有冲突,还是用自定义方法比较合适
    //这个自动转换用于将true和false换成显示和隐藏
    @BindingConversion
    @JvmStatic
    fun convertBooleanToInt(boolean: Boolean): Int {
        Log.i("自定义转换")
        return if (boolean) View.VISIBLE else View.GONE
    }

}