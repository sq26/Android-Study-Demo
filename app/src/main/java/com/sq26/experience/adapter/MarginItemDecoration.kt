package com.sq26.experience.adapter

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sq26.experience.util.kotlin.dpToPx

/**
 * 给RecyclerView的每个item设置间距
 * 这个设置会使item的根视图的padding失效
 * dp:间距大小,默认值8dp
 */
class MarginItemDecoration(context: Context, dp: Int = 8) :
    RecyclerView.ItemDecoration() {
    //获取px
    private val px = context.resources.displayMetrics.dpToPx(dp)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        //获取当前view的下标
        val position = parent.getChildAdapterPosition(view)
        //判断布局类型
        when (parent.layoutManager) {
            //线性布局
            is LinearLayoutManager -> {
                //判断方向
                if ((parent.layoutManager as LinearLayoutManager).orientation == LinearLayoutManager.VERTICAL) {
                    //纵向,只给第一个子视图加上边距
                    if (position == 0)
                        outRect.top = px
                    outRect.left = px
                } else {
                    //横向,只给第一个子视图的左边距
                    if (position == 0)
                        outRect.left = px
                    outRect.top = px
                }
            }
            //网格布局和瀑布流
            is GridLayoutManager,
            is StaggeredGridLayoutManager -> {
                //获取列数
                val spanCount = (parent.layoutManager as GridLayoutManager).spanCount
                //下标小于列数表示处于第一行,只给第一行加上边距
                if (position < spanCount)
                    outRect.top = px
                //下标可以被列数整除表示处于所处行的第一列,只给每行的第一列加左边距
                if (position % spanCount == 0)
                    outRect.left = px
            }
        }
        //其余的都加右边距和下边距
        outRect.right = px
        outRect.bottom = px
    }
}