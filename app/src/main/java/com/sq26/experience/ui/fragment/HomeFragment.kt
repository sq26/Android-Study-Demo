package com.sq26.experience.ui.fragment

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.sq26.experience.R
import com.sq26.experience.adapter.HomeMenuAdapter
import com.sq26.experience.databinding.FragmentHomeBinding
import com.sq26.experience.util.Log
import com.sq26.experience.viewmodel.HomeViewModel
import com.sq26.experience.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    //获取数据模型
    private val homeViewModel: HomeViewModel by viewModels()
    //获取全局数据模型
    private val mainViewModel: MainViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //加入返回监听,监听放在onCreate中,放在onCreateView中会重复添加
        requireActivity().onBackPressedDispatcher.addCallback {
            //判断侧边栏有没有打开
            if (binding.drawerLayout.isOpen) {
                //打开了,就关闭侧边栏
                binding.drawerLayout.closeDrawers()
            } else {
                //没打开就禁用返回监听
                isEnabled = false
                //调用返回
                if (!findNavController().popBackStack())
                //如果返回false就说明已经到顶层了,直接调用关闭
                    activity?.finish()
            }
        }
    }

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel.isInit.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToStartFragment())
        }
        //获取数据绑定
        binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        ).apply {
            //绑定生命周期
            lifecycleOwner = viewLifecycleOwner
            //绑定视图模型
            viewModel = homeViewModel
            //绑定自定义事件
            event = object : Event {
                override fun onClick() {
                    Log.i("点击事件")
                }
            }
            //设置导航键的点击事件
            toolbar.setNavigationOnClickListener {
                //弹出侧边栏
                findNavController().navigateUp(drawerLayout)
            }
            //获取菜单适配器
            val homeMenuAdapter = HomeMenuAdapter(homeViewModel)
            //设置适配器
            menuRecyclerView.adapter = homeMenuAdapter
            //设置数据监听
            homeViewModel.homeMenuList.observe(viewLifecycleOwner) {
                homeMenuAdapter.submitList(it)
                //菜单列表刷新时关闭侧边栏
                drawerLayout.closeDrawers()
            }
            //获取菜单类型适配器
            val homeMenuTypeAdapter = HomeMenuAdapter(homeViewModel)
            //设置适配器
            menuTypeRecyclerView.adapter = homeMenuTypeAdapter
            //设置数据监听
            homeViewModel.homeMenuTypeList.observe(viewLifecycleOwner) {
                homeMenuTypeAdapter.submitList(it)
            }
        }

        return binding.root
    }

    interface Event {
        fun onClick()
    }
}


