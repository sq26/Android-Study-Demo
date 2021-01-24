package com.sq26.experience.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonAdapter
import com.sq26.experience.adapter.ItemTouchHelperCallback
import com.sq26.experience.adapter.RecyclerView1Adapter
import com.sq26.experience.adapter.ViewHolder
import com.sq26.experience.databinding.ActivityRecyclerViewBinding
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
            val adapter = RecyclerView1Adapter()
            recyclerView.adapter = adapter

            viewModel.getQueryAll().observe(this@RecyclerViewActivity) {
                adapter.submitList(it)
            }
        }
    }

//    private fun initView() {
//        //设置布局管理器
//        //创建适配器
//        commonAdapter = object : CommonAdapter(R.layout.item_recyclerview_item, jsonArray) {
//            override fun bindViewHolder(
//                viewHolder: ViewHolder,
//                jsonObject: JSONObject,
//                position: Int,
//                payload: Any
//            ) {
//                //等空说明需要刷新全部,不是空就不刷新视图
//                if (payload == null) {
//                    //创建动画
//                    val animation =
//                        AnimationUtils.loadAnimation(context, R.anim.anim_recycler_item_show)
//                    //给item设置显示动画
//                    viewHolder.itemView.startAnimation(animation)
//                    //
//                    viewHolder.setText(R.id.text, jsonObject.getString("text"))
//                    val cardView = viewHolder.getView<CardView>(R.id.cardView)
//                    val r = random.nextInt(256)
//                    val g = random.nextInt(256)
//                    val b = random.nextInt(256)
//                    cardView.setCardBackgroundColor(Color.rgb(r, g, b))
//                }
//                //无论如何都刷新处理按钮
//                viewHolder.itemView.setOnClickListener {
//                    Log.d("点击的下标", "" + position)
//                    Log.d("文本是", "" + jsonArray.getJSONObject(position).getString("text"))
//                    AlertDialog.Builder(this@RecyclerViewActivity)
//                        .setMessage(
//                            """
//    点击的下标$position
//    文本是${jsonArray.getJSONObject(position).getString("text")}
//    """.trimIndent()
//                        )
//                        .show()
//                }
//            }
//        }
//        //给适配器item设置显示动画
////        commonAdapter.setItemAnimation(animation);
//        //设置纵向分割线
//        recyclerView!!.addItemDecoration(
//            DividerItemDecoration(
//                this,
//                DividerItemDecoration.VERTICAL
//            )
//        )
//        //设置横向分割线
//        recyclerView!!.addItemDecoration(
//            DividerItemDecoration(
//                this,
//                DividerItemDecoration.HORIZONTAL
//            )
//        )
//        //设置适配器
//        recyclerView!!.adapter = commonAdapter
//        //创建item触摸气泡回调
//        val callback: ItemTouchHelper.Callback = ItemTouchHelperCallback(commonAdapter)
//        //创建iten触摸气泡
//        val itemTouchHelper = ItemTouchHelper(callback)
//        //设置recyclerView
//        itemTouchHelper.attachToRecyclerView(recyclerView)
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_lookover, menu)
        return true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_add -> {
                viewModel.insert()
//                val jsonObject = JSONObject()
//                jsonObject["text"] = 2
//                jsonArray.add(2, jsonObject)
//                commonAdapter!!.notifyItemInserted(2)
//                commonAdapter!!.notifyItemRangeChanged(2, jsonArray.size - 2 + 1, true)
            }
            R.id.action_look_over -> if (binding.recyclerView.layoutManager is GridLayoutManager) {
                item.icon = getDrawable(R.drawable.ic_view_list_white_24dp)
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
            } else {
                item.icon = getDrawable(R.drawable.ic_view_module_white_24dp)
                binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}