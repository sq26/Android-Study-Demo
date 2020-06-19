package com.sq26.experience.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class TestView extends View {
    //定义一个画笔对象(同时也是定义图形的边框)
    Paint paint = new Paint();
    //定义一个路径
    Path path = new Path();

    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置画笔颜色
        paint.setColor(0xFFFF0000);
        //设置填充样式
        paint.setStyle(Paint.Style.STROKE);
        //设置画笔宽度
        paint.setStrokeWidth(5);

        /*画圆
         * cx:指圆心的x轴坐标
         * cy:指圆心的y轴坐标(在Android的屏幕上值为0是在屏幕的最顶部)
         * radius:圆的半径
         * paint:画笔
         */
//        canvas.drawCircle(0, 0, 150, paint);
        /*画直线
         * startX:起始点X坐标
         * startY:起始点Y坐标
         * stopX:终点X坐标
         * stopY:终点Y坐标
         * paint:画笔
         */
//        canvas.drawLine(100, 100, 200, 200, paint);
        /*点
         *x: 点的X坐标
         *y: 点的Y坐标
         * paint:画笔
         */
//        canvas.drawPoint(100, 100, paint);
        /*矩形
         *  left:左边界距离画布左边界的距离
         *  top:上边界距离画布上边界的距离
         *  right:右边界距离画布左边界的距离
         *  bottom:底边界距离画布上边界的距离
         * paint:画笔
         */
//        canvas.drawRect(10, 10, 200, 300, paint);
//        ---------------------------------------
        //绘制直线路径
        //定义一个路径的起点
//        path.moveTo(500, 100);
        //定义一个路径的中点
//        path.lineTo(100, 700);
//        path.lineTo(900, 350);
        //连接最后一个中点和起点
//        path.close();
        //绘制路径
//        canvas.drawPath(path, paint);
//        ---------------------------------------
        /*绘制弧线(想象在一个矩形内有一个贴着四个边的椭圆)
         *  left:左边界距离画布左边界的距离
         *  top:上边界距离画布上边界的距离
         *  right:右边界距离画布左边界的距离
         *  bottom:底边界距离画布上边界的距离
         *  startAngle:弧线开始的位置,已x轴正方向为0°(就是3点钟位置)
         *  sweepAngle:弧线持续(结束)的角度
         *  forceMoveTo:是否将弧线的起始点作为绘制的起始位置(我个人理解是,是否将弧的起点作为路径的中点或起点)
         */

        Region region = new Region(50,50,200,100);

        canvas.drawPath(path, paint);
    }
}
