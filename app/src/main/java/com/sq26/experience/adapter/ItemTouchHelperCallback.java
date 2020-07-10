package com.sq26.experience.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private RecyclerViewAdapter recyclerViewAdapter;
    //默认的内容View的viewType是1
    private int itemViewType = 1;

    public ItemTouchHelperCallback(RecyclerViewAdapter recyclerViewAdapter) {
        this.recyclerViewAdapter = recyclerViewAdapter;
    }
    public ItemTouchHelperCallback(RecyclerViewAdapter recyclerViewAdapter,int itemViewType) {
        this.recyclerViewAdapter = recyclerViewAdapter;
        this.itemViewType = itemViewType;
    }

    /**
     * 应该返回一个复合标志，该标志定义每个状态（空闲，滑动，拖动）中启用的移动方向。
     * 您可以使用makeMovementFlags(int, int) 或来代替手动编写此标志makeFlag(int, int)。
     * 该标志由3组8位组成，其中前8位用于IDLE状态，后8位用于SWIPE状态，后8位用于DRAG状态。每个8位段可以通过对中定义的方向标志进行“或”运算来构造 ItemTouchHelper。
     * 例如，如果您希望它允许向左和向右滑动，但只允许通过向右滑动开始滑动，则可以返回：
     * makeFlag（ACTION_STATE_IDLE，RIGHT）| makeFlag（ACTION_STATE_SWIPE，LEFT | RIGHT）;
     * 这意味着，在“空闲”时允许右移动，而在滑动时允许左右移动。
     *
     * @param recyclerView ItemTouchHelper附加到的RecyclerView。
     * @param viewHolder   需要移动信息的ViewHolder。
     * @return 标志，指定在此ViewHolder上允许哪些移动。
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //判断是否网格布局管理器
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            //可以拖动项目的方向。(上下左右都可以)
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            //可以滑动项目的方向(0不能滑动)。
            int swipeFlags = 0;
            //创建运动标记的便捷方法。
            return makeMovementFlags(dragFlags, swipeFlags);
        } else
            //判断是否线性布局管理器
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                //判断viewType类型是否等于1(因为默认的内容View的viewType就是1,等1表示他是内容item)
                if (viewHolder.getItemViewType() == itemViewType) {
                    //可以拖动项目的方向。(上下左右都可以)
                    int dragFlags = 0;
                    //可以滑动项目的方向(0不能滑动)。
                    int swipeFlags = 0;
                    //判断是否纵向的LinearLayoutManager
                    if (((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation() == LinearLayoutManager.VERTICAL) {
                        //设置上下拖动
                        dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                        //设置左右滑动
                        swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                    } else {
                        //设置左右拖动
                        dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                        //设置上下滑动
                        swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    }
                    //创建运动标记的便捷方法。
                    return makeMovementFlags(dragFlags, swipeFlags);
                } else {
                    //其他的不能滑动
                    return 0;
                }
            }//其他
            else {
                return 0;
            }
    }

    /**
     * 当ItemTouchHelper想要将拖动的项目从其旧位置移动到新位置时调用。
     * 如果此方法返回true，则ItemTouchHelper假定viewHolder已移动到targetViewHolder（ViewHolder#getAdapterPosition()）的适配器位置。
     * 如果您不支持拖放，则永远不会调用此方法。
     *
     * @param recyclerView ItemTouchHelper附加到的RecyclerView。
     * @param viewHolder   由用户拖动的ViewHolder。
     * @param target       在其上拖动当前活动项目的ViewHolder。
     * @return 如果将viewHolder移至的适配器位置， 则为True target。
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        //判断是否相同的viewType,不相同不可以拖动
        if (viewHolder.getItemViewType() != target.getItemViewType())
            return false;
        //调用recyclerViewAdapter的移动方法
        recyclerViewAdapter.onItemViewMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /**
     * 当用户滑动ViewHolder时调用。
     * 如果您要从方法返回相对方向（START，END） getMovementFlags(RecyclerView, ViewHolder)，则此方法还将使用相对方向。否则，它将使用绝对方向。
     * ItemTouchHelper将保留对视图的引用，直到将其与RecyclerView分离为止。分离后，ItemTouchHelper将立即调用 clearView(RecyclerView, ViewHolder)。
     *
     * @param viewHolder 已被用户刷过的ViewHolder。
     * @param direction  将ViewHolder滑动的方向。这是一个 UP，DOWN， LEFT或RIGHT。如果您的 方法返回相对标志而不是/ ；方向也是相对的。（或）。 getMovementFlags(RecyclerView, ViewHolder)LEFTRIGHTSTARTEND
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        recyclerViewAdapter.onClearItemView(viewHolder.getAdapterPosition());
    }
}
