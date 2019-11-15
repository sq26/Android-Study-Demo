package com.sq26.experience.util;

import android.content.Context;
import android.widget.Toast;

public class AppUtil {

    /**
     * 弹出基础Toast提示框
     *
     * @param context 上下文
     * @param resId   显示文本的resId
     */
    public static void showToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
