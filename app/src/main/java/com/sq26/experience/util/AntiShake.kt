package com.sq26.experience.util

import android.annotation.SuppressLint
import android.view.View
import java.util.*

/**
 * 创建时间：2019/6/6 12:25
 * 作者：syq
 * 描述：防重复点击公共方法
 */
object AntiShake {
    private data class AntiShakeItem(
        //等待时间
        val waitingTime: Long,
        //第一次点击时间
        val currentTime: Long
    )

    //用来保存点击事件view 的集合key是viewID,value是上次的点击时间
    private val viewIdMap = mutableMapOf<Long, AntiShakeItem>()

    /**
     * @param viewId      view的id
     * @param waitingTime 两次点击间的等待时间
     * @return 是否重复点击, true是重复点击
     */
    @JvmOverloads
    fun check(viewId: Long, waitingTime: Long = 1000): Boolean {
        //获取当前时间
        val currentTime = System.currentTimeMillis()
        //获取view上一次的点击保存的map对象
        val oneMap = viewIdMap[viewId]
        //判断上一次的点击时间是否为空
        return if (oneMap != null) {
            //不为空说明有点击过
            //获取view上一次的点击时间
            val oneClickTime = oneMap.currentTime
            val oneWaitingTime = oneMap.waitingTime
            //用当前时间减去上次保存时间看看是不是大于等待时间
            if (currentTime - oneClickTime > oneWaitingTime) {
                //大于等待时间说明不是重复点击,并刷新点击时间
                viewIdMap[viewId] = AntiShakeItem(waitingTime, currentTime)
                false
            } else {
                //小于等待时间说明是重复点击
                true
            }
        } else {
            //为空说明没点击过
            //判断view的集合长度是否大于10
            if (viewIdMap.size > 10) {
                //长度超过10,去除靠前的viewID,以避免内存占用
                viewIdMap.remove(viewIdMap.keys.first())
            }
            //保存初次的点击时间
            viewIdMap[viewId] = AntiShakeItem(waitingTime, currentTime)
            //属于初次点击
            false
        }
    }
}
//给view加上防手抖点击
fun View.setOnClickAntiShake(onClick: (View) -> Unit) {
    val id = System.nanoTime()
    setOnClickListener {
        if (!AntiShake.check(id))
            onClick(it)
    }
}