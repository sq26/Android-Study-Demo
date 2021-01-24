package com.sq26.experience.ui.activity

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.*
import com.sq26.experience.R
import com.sq26.experience.adapter.RecyclerView1Adapter
import com.sq26.experience.databinding.ActivityRecyclerViewBinding
import com.sq26.experience.util.Log
import com.sq26.experience.viewmodel.RecyclerViewViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class RecyclerViewActivity : AppCompatActivity() {
    //    private val random = Random()
    private lateinit var binding: ActivityRecyclerViewBinding
    private val viewModel: RecyclerViewViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityRecyclerViewBinding>(
            this,
            R.layout.activity_recycler_view
        ).apply {
            //设置标题名
            toolbar.title = "RecyclerView"
            setSupportActionBar(toolbar)
            //设置布局管理器
            recyclerView.layoutManager = LinearLayoutManager(this@RecyclerViewActivity)
//        //设置横向分割线
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    this@RecyclerViewActivity,
                    DividerItemDecoration.HORIZONTAL
                )
            )
//        //设置纵向分割线
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    this@RecyclerViewActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
            //声明adapter
            val adapter = RecyclerView1Adapter()
            //设置adapter
            recyclerView.adapter = adapter
            //设置触摸滑动帮手
            ItemTouchHelper(object : ItemTouchHelper.Callback() {
                //该方法用于返回可以滑动的方向，比如说允许从右到左侧滑，允许上下拖动等。
                //滑动和拖动是不能兼容的,不可以向左右拖动的同时又想左右滑动,同时设置的时候拖动的优先级大于滑动
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    //dragFlags:item可以拖动的方向。(上下左右都可以)
                    //swipeFlags:item可以内部滑动的方向(上下左右都可以)
                    return if (recyclerView.layoutManager is GridLayoutManager)
                    //是网格布局就只允许拖动不能滑动
                        makeMovementFlags(
                            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                            0
                        )
                    else if (recyclerView.layoutManager is LinearLayoutManager)
                    //是线性布局,判断是纵向还是横向
                        if ((recyclerView.layoutManager as LinearLayoutManager).orientation == LinearLayoutManager.VERTICAL)
                        //纵向就设定上下拖动,左右滑动
                            makeMovementFlags(
                                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                            )
                        else
                        //横向就是左右拖动,上下滑动
                            makeMovementFlags(
                                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                                ItemTouchHelper.UP or ItemTouchHelper.DOWN
                            )
                    else
                    //其他布局不能拖动也不能滑动
                        makeMovementFlags(
                            0, 0
                        )
                }

                //当用户拖动一个Item进行移动从旧的位置到新的位置的时候会调用该方法
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    //这里处理item交换位置
                    adapter.notifyItemMoved(
                        viewHolder.adapterPosition,
                        target.adapterPosition
                    )
                    //这里处理数据交换位置
//                    val new = adapter.currentList[viewHolder.adapterPosition]
//                    adapter.currentList[viewHolder.adapterPosition] = adapter.currentList[target.adapterPosition]
//                    adapter.currentList[target.adapterPosition] = new
                    //返回true表示处理移动完毕,返回false拖动的item不能与新位置的item交换位置
                    return true
                }

                //当用户滑动Item时，会调用该方法,direction是划出的方向
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    //手指触摸滑动的距离达到RecyclerView宽度的一半时，再松开手指，此时该Item会继续向原先滑动方向滑过去并且调用onSwiped方法进行删除，否则会反向滑回原来的位置。
                    //删除当前列的item
                    Log.i(viewHolder.adapterPosition.toString(), "adapterPosition")
                    Log.i(viewHolder.layoutPosition.toString(), "layoutPosition")
                    Log.i(viewHolder.oldPosition.toString(), "oldPosition")
                    adapter.notifyItemChanged(viewHolder.adapterPosition)
//                    viewModel.delete((viewHolder as RecyclerView1Adapter.ViewHolder).getItem())
                }

                //是否支持长按滑动
                override fun isLongPressDragEnabled(): Boolean {
                    //返回true表示支持长按后滑动,默认值是true,返回false表示可以直接滑动
                    return true
                }

                //是否可以滑动
                override fun isItemViewSwipeEnabled(): Boolean {
                    //返回true表示可以滑动,并会调用onSwiped方法,默认值是true,false则完全不会滑动
                    return true
                }

                //从静止状态变为拖拽或者滑动的时候会回调该方法，参数actionState表示当前的状态。
                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
//                    viewHolder?.itemView?.translationX = 0f
                }

                //我们可以在这个方法内实现我们自定义的交互规则或者自定义的动画效果。
                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

                //当用户操作完毕某个item并且其动画也结束后会调用该方法,在这里初始化或恢复item的状态,防止由于复用而产生的显示错乱问题。
                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                }
            }).attachToRecyclerView(recyclerView)
            //获取实时列表信息
            viewModel.getQueryAll().observe(this@RecyclerViewActivity) {
                adapter.submitList(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_lookover, menu)
        return true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //点击导航按钮返回上一页
            android.R.id.home -> onBackPressed()
            //添加新的item
            R.id.action_add -> {
                viewModel.insert()
            }
            //切换布局管理器
            R.id.action_look_over ->
                //设置布局管理器
                binding.recyclerView.layoutManager =
                        //判断当前是网格布局,就切换到线性布局,是线性布局就切换到网格布局,if判断的最后一行是返回值
                    if (binding.recyclerView.layoutManager is GridLayoutManager) {
                        item.icon = getDrawable(R.drawable.ic_view_list_white_24dp)
                        LinearLayoutManager(this)
                    } else {
                        item.icon = getDrawable(R.drawable.ic_view_module_white_24dp)
                        GridLayoutManager(this, 3)
                    }
        }
        return super.onOptionsItemSelected(item)
    }
}