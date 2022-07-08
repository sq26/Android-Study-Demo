package com.sq26.experience.binding

import android.view.View
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.sq26.experience.adapter.MarginItemDecoration
import com.sq26.experience.util.AntiShake
import com.sq26.experience.util.Log
import com.sq26.experience.util.OnClickAntiShakeListener
import com.sq26.experience.util.i
import com.sq26.experience.util.kotlin.getFileSizeStr
import java.text.SimpleDateFormat

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
    fun setVis(view: View, b: Boolean?) {
        b?.let {
            view.isVisible = it
        }
        Log.i("自定义属性方法")
    }

    //让MaterialCheckBox属性可以绑定开关监听
    @BindingAdapter("android:onCheckedChange")
    //需要加JvmStatic才能创建使用java的静态对象
    @JvmStatic
    fun setOnCheckedChange(
        view: MaterialCheckBox,
        listener: CompoundButton.OnCheckedChangeListener?
    ) {
        listener?.let {
            view.setOnCheckedChangeListener(it)
        }
    }

    //单击防手抖点击事件
    @BindingAdapter("onClickAntiShake")
    //需要加JvmStatic才能创建使用java的静态对象
    @JvmStatic
    fun setOnClickAntiShake(view: View, listener: View.OnClickListener?) {
        listener?.let { l ->
            val id = System.nanoTime()
            "${id}检查有没有被重复调用".i()
            view.setOnClickListener {
                if (!AntiShake.check(id))
                    l.onClick(it)
            }
        }
    }

    //给RecyclerView的每个item设置间距
    @BindingAdapter("itemMargin")
    @JvmStatic
    fun setItemMargin(view: RecyclerView, dp: Int?) {
        dp?.let {
            view.addItemDecoration(MarginItemDecoration(view.context, it))
        }
    }

    //自定义类型转换
    //当接收属性需要Int类型,输入的却是boolean类型时,自动转换类型,感觉这种方法容易有冲突,还是用自定义方法比较合适
    //这个自动转换用于将true和false换成显示和隐藏
    @BindingConversion
    @JvmStatic
    fun convertBooleanToInt(boolean: Boolean): Int {
        return if (boolean) View.VISIBLE else View.GONE
    }

    //自定义类型转换
    //当接收属性需要Int类型,输入的却是boolean类型时,自动转换类型,感觉这种方法容易有冲突,还是用自定义方法比较合适
    //这个自动转换用于将true和false换成显示和隐藏
    @BindingConversion
    @JvmStatic
    fun convertStringToLong(string: String): Long {
        return string.toLong()
    }

    //自定义类型转换
    //这个自动转换用于将Long转成string类型
    @BindingConversion
    @JvmStatic
    fun convertLongToString(long: Long): String {
        return long.toString()
    }

}

object StringUtil {
    @JvmStatic
    fun convertLongToDateTime(long: Long): String = SimpleDateFormat.getDateInstance().format(long)

    @JvmStatic
    fun getFileSizeStr(long: Long): String = long.getFileSizeStr()

    @JvmStatic
    fun size(array: Array<*>): String = array.size.toString()
}
