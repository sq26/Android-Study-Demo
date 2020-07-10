package com.sq26.experience.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.sq26.experience.R;

public class ProgressDialog {
    //记录主视图上下文
    private Context context;
    //封装一个AlertDialog
    private AlertDialog alertDialog;
    //封装textView用于随时修改文本
    private TextView message;

    //构造方法,最低只需要上下文
    public ProgressDialog(Context context) {
        this.context = context;
        initView(context.getString(R.string.Loading));
    }

    //构造方法,可以预设显示的文本
    public ProgressDialog(Context context, String msg) {
        this.context = context;
        initView(msg);
    }

    //视图初始化方法
    private void initView(String msg) {
        //判断alertDialog有没有被创建
        if (alertDialog == null) {
            //创建一个AlertDialog
            alertDialog = new AlertDialog.Builder(context).create();
            //创建一个视图
            View view = View.inflate(context, R.layout.dialog_progress_circle, null);
            //将视图中的文本控件提取封装
            message = view.findViewById(R.id.text);
            //设置文本
            message.setText(msg);
            //设置alertDialog不能点击返回键和点击屏幕关闭
            alertDialog.setCancelable(false);
            //设置alertDialog的视图
            alertDialog.setView(view);
        }
    }

    //显示alertDialog
    public ProgressDialog show() {
        alertDialog.show();
        return this;
    }

    //设置alertDialog不能点击返回键和点击屏幕关闭
    public ProgressDialog setCancelable(boolean flag) {
        alertDialog.setCancelable(flag);
        return this;
    }

    //动态设置alertDialog中的文本(string)
    public ProgressDialog setMessage(String msg) {
        message.setText(msg);
        return this;
    }

    //动态设置alertDialog中的文本(res)
    public ProgressDialog setMessage(int resId) {
        message.setText(resId);
        return this;
    }

    //隐藏并关闭alertDialog
    public ProgressDialog dismiss() {
        alertDialog.dismiss();
        return this;
    }
}
