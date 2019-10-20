package com.sq26.experience.util;


import android.annotation.SuppressLint;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建时间：2019/6/6 12:25
 * 作者：syq
 * 描述：防重复点击公共方法
 */
public class AntiShake {
    //用来保存点击事件view 的集合key是viewID,value是上次的点击时间
    @SuppressLint("UseSparseArrays")
    private static Map<Integer, Map<String, Long>> viewIdMap = new HashMap<>();


    public static boolean check(int viewId) {
        return check(viewId, 1000);
    }

    /**
     * @param viewId      view的id
     * @param waitingTime 两次点击间的等待时间
     * @return 是否重复点击, true是重复点击
     */
    public static boolean check(int viewId, long waitingTime) {
        //获取当前时间
        long currentTime = Calendar.getInstance().getTimeInMillis();
        //获取view上一次的点击保存的map对象
        Map<String, Long> oneMap = viewIdMap.get(viewId);
        //判断上一次的点击时间是否为空
        if (oneMap != null) {
            //不为空说明有点击过
            //获取view上一次的点击时间
            Long oneClickTime = oneMap.get("currentTime");
            Long oneWaitingTime = oneMap.get("waitingTime");
            //用当前时间减去上次保存时间看看是不是大于等待时间
            if (currentTime - oneClickTime > oneWaitingTime) {
                //大于等待时间说明不是重复点击,并刷新点击时间
                Map<String, Long> map = new HashMap<>();
                map.put("currentTime", currentTime);
                map.put("waitingTime", waitingTime);
                viewIdMap.put(viewId, map);
                return false;
            } else {
                //小于等待时间说明是重复点击
                return true;
            }
        } else {
            //为空说明没点击过
            //判断vuew的集合长度是否大于10
            if (viewIdMap.size() > 10) {
                //长度超过10,去除靠前的viewID,以避免内存占用
                viewIdMap.remove(viewIdMap.keySet().toArray()[0]);
            }
            //保存初次的点击时间
            Map<String, Long> map = new HashMap<>();
            map.put("currentTime", currentTime);
            map.put("waitingTime", waitingTime);
            viewIdMap.put(viewId, map);
            //不属于初次点击
            return false;
        }
    }
}