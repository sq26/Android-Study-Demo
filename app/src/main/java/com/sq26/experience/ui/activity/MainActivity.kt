package com.sq26.experience.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.sq26.experience.R
import androidx.databinding.DataBindingUtil.setContentView
import com.sq26.experience.adapter.CommonListAdapter
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.databinding.ActivityMainBinding
import com.sq26.experience.databinding.ItemRecyclerviewBinding
import com.sq26.experience.entity.HomeMenu
import com.sq26.experience.entity.HomeMenuDiffCallback
import com.sq26.experience.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            lifecycleOwner = this@MainActivity
            viewModel = mainViewModel

            val toggle = ActionBarDrawerToggle(
                this@MainActivity,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            //设置导航键的点击事件
            toolbar.setNavigationOnClickListener {
                //弹出侧边栏
                drawerLayout.openDrawer(GravityCompat.START)

////                try {
//                    val s:String? = null
//                    s!!.toString()
////                }catch (e:Exception){
////                    e.printStackTrace()
////                }
            }
            //获取菜单适配器
            val homeMenuAdapter = object : CommonListAdapter<HomeMenu>(HomeMenuDiffCallback()) {
                override fun createView(parent: ViewGroup, viewType: Int): CommonListViewHolder<*> {
                    return object : CommonListViewHolder<ItemRecyclerviewBinding>(
                        ItemRecyclerviewBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    ) {
                        init {
                            v.setClickListener {
                                v.homeMenu?.let {
                                    //设置要跳转的页面
                                    mainViewModel.startTo(this@MainActivity, it.id)
                                }
                            }
                        }

                        override fun bind(position: Int) {
                            v.homeMenu = getItem(position)
                        }
                    }
                }
            }
            //设置适配器
            menuRecyclerView.adapter = homeMenuAdapter
            //设置数据监听
            mainViewModel.homeMenuList.observe(this@MainActivity) {
                homeMenuAdapter.submitList(it)
                //菜单列表刷新时关闭侧边栏
                drawerLayout.closeDrawers()
            }
            //获取菜单类型适配器
            val homeMenuTypeAdapter = object : CommonListAdapter<HomeMenu>(HomeMenuDiffCallback()) {
                override fun createView(parent: ViewGroup, viewType: Int): CommonListViewHolder<*> {
                    return object : CommonListViewHolder<ItemRecyclerviewBinding>(
                        ItemRecyclerviewBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    ) {
                        init {
                            v.setClickListener {
                                v.homeMenu?.let {
                                    //刷新菜单
                                    mainViewModel.refreshHomeMenuList(it.id)
                                }
                            }
                        }

                        override fun bind(position: Int) {
                            v.homeMenu = getItem(position)
                        }
                    }
                }
            }
            //设置适配器
            menuTypeRecyclerView.adapter = homeMenuTypeAdapter
            //设置数据监听
            mainViewModel.homeMenuTypeList.observe(this@MainActivity) {
                homeMenuTypeAdapter.submitList(it)
            }
            //加入返回监听,监听放在onCreate中,放在onCreateView中会重复添加
            onBackPressedDispatcher.addCallback {
                //判断侧边栏有没有打开
                if (drawerLayout.isOpen) {
                    //打开了,就关闭侧边栏
                    drawerLayout.closeDrawers()
                } else {
                    //没打开就禁用返回监听
                    isEnabled = false
                    //调用返回
                    finish()
                }
            }
        }
    }
}