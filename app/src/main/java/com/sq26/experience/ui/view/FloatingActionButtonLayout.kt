package com.sq26.experience.ui.view

import android.animation.Animator
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sq26.experience.R
import com.sq26.experience.util.i
import com.sq26.experience.util.setOnClickAntiShake
import kotlin.math.*

/**
 * 自定义视图,用于展开收起其他控件
 */
@SuppressLint("ClickableViewAccessibility", "ObjectAnimatorBinding")
class FloatingActionButtonLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {
    //是否展开
    private var expand = false

    //内容视图
    private val linearLayout: LinearLayout

    init {
        //初始化展开收起按钮
        val menu = FloatingActionButton(context)
        //设置边距
        menu.layoutParams =
            MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        16f,
                        resources.displayMetrics
                    ).toInt()
                )
            }
        //设置按钮图标
        menu.setImageResource(R.drawable.ic_baseline_add_24)
        //设置点击事件
        menu.setOnClickAntiShake {
            //切换展开状态
            expand = !expand
            //旋转动画
            if (expand)
                it.animate().rotation(45f).setDuration(100).start()
            else
                it.animate().rotation(0f).setDuration(100).start()
            //展开收起的动画
            expandAnimate()
        }
        //设置图标的颜色
        menu.imageTintList = ColorStateList.valueOf(Color.WHITE)
        //添加按钮
        addView(menu)
        //创建滚动视图
        val scrollView = NestedScrollView(context)
        //给滚动视图设置右边距
        scrollView.layoutParams =
            MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(
                    0, 0, TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        16f,
                        resources.displayMetrics
                    ).toInt(), 0
                )
            }
        //添加滚动视图
        addView(scrollView)
        //创建内容视图
        linearLayout = LinearLayout(context)
        //设置纵向
        linearLayout.orientation = LinearLayout.VERTICAL
        //右对齐
        linearLayout.gravity = GravityCompat.END
        //透明的设置为零
        linearLayout.alpha = 0f
        //延y轴缩放至零
        linearLayout.scaleY = 0f
        //延y轴向下移动自身的高度
        linearLayout.translationY = linearLayout.measuredHeight.toFloat()
        //创建容器动画
        val layoutTransition = LayoutTransition()
        val objectAnimator = ObjectAnimator.ofFloat(null, "alpha", 1f, 0f)
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, objectAnimator)
        //设置容器动画
        linearLayout.layoutTransition = layoutTransition
        //把容器添加进滚动视图
        scrollView.addView(linearLayout)
    }

    //新增控件
    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        //前两个控件是容器内部的,调用默认的addView,其他的直接添加入内容视图
        if (childCount > 1) {
            linearLayout.addView(child, index, params)
        } else {
            super.addView(child, index, params)
        }
    }

    //移除控件,直接调用内容的移除
    override fun removeView(view: View?) {
        linearLayout.removeView(view)
    }

    //移除控件,直接调用内容的移除
    override fun removeViewAt(index: Int) {
        linearLayout.removeViewAt(index)
    }

    //计算视图大小
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //宽度模式
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        //宽度尺寸
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        //高度模式
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        //高度尺寸
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        //所需要的最低高宽
        var width = 0
        var height = 0
        //遍历子元素
        repeat(childCount) {
            //获取子元素
            val view = getChildAt(it)
            if (view.isVisible) {
                //计算子元素大小
                measureChild(view, widthMeasureSpec, heightMeasureSpec)
                val lp = view.layoutParams as MarginLayoutParams
                //获取子元素宽度和高度
                val viewWidth = view.measuredWidth + lp.marginStart + lp.marginEnd
                val viewHeight = view.measuredHeight + lp.topMargin + lp.bottomMargin
                //计算布局最大宽度和高度
                width = max(width, viewWidth)
                //累加高度
                height += viewHeight
            }
        }
        //设置布局最大宽度和高度(MeasureSpec.EXACTLY==具体值或最大值)
        setMeasuredDimension(
            if (widthMode == MeasureSpec.EXACTLY) widthSize else width,
            if (heightMode == MeasureSpec.EXACTLY) heightSize else height
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //获取底边位置(减去边距)
        var button = b - marginBottom
        repeat(childCount) {
            val view = getChildAt(it)
            //获取边距信息
            val lp = view.layoutParams as MarginLayoutParams
            //获取子元素宽度和高度
            val viewWidth = view.measuredWidth
            val viewHeight = view.measuredHeight
            //设置上下左右的边框位置
            view.layout(
                max(r - marginEnd - lp.marginEnd - viewWidth, 0),
                max(button - lp.bottomMargin - viewHeight, 0),
                min(r - marginEnd - lp.marginEnd, r),
                min(button - lp.bottomMargin, button)
            )
            //累加底边位置
            button -= (viewHeight + lp.topMargin + lp.bottomMargin)
        }

    }

    //展开动画
    private fun expandAnimate() {
        //设置内容的所有子元素的可点击状态跟随展开收起状态
        repeat(linearLayout.childCount) {
            linearLayout.getChildAt(it).isClickable = expand
        }
        if (expand) {
            //展开动画
            linearLayout.animate()
                .alpha(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(300)
                .start()
        } else {
            //收起动画
            linearLayout.animate()
                .alpha(0f)
                .scaleY(0f)
                .translationY(linearLayout.measuredHeight.toFloat())
                .setDuration(300)
                .start()
        }
    }

    //设置布局参数为带有边距的MarginLayoutParams
    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    fun contextCount(): Int = linearLayout.childCount

}