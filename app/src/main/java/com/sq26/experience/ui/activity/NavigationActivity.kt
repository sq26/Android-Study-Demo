package com.sq26.experience.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.navigation.findNavController
import com.sq26.experience.NavigationNavigationDirections
import com.sq26.experience.databinding.ActivityNavigationBinding
import com.sq26.experience.util.Log

//Navigation的基本使用
//目前发现Navigation只能往前跳转,和在fragment中向上返回,其他操作需要配合finish()
class NavigationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNavigationBinding
//    private val viewModel: NavigationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //获取navController
//        navController = Navigation.findNavController(this,R.id.nav_host_fragment)
//        navController = findNavController(R.id.nav_host_fragment)
//        navController = binding.navHostFragment.findNavController()
        binding.toolbar.title = "导航"
        setSupportActionBar(binding.toolbar)
//        Log.i("liveData1:${viewModel.text}")
        //声明NavController,整个 Navigation 架构中 最重要的核心类，我们所有的导航行为都由 NavController 处理
        //宿主activity提前申明获取navController的方法
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        var  navController = navHostFragment.navController

//        binding.button1.setOnClickListener {
//            //binding.navHostFragment.findNavController()在视图没有显示之前调用会报下标越界错误
//            //修改viewModel的text为111,在跳转后的fragmen中获取
//            viewModel.text = "111"
//            //全局跳转到BlankFragment
//            val action = NavigationNavigationDirections.actionGlobalBlankFragment()
//
//            binding.navHostFragment.findNavController().navigate(action)
//        }
//        binding.button2.setOnClickListener {
//            //全局跳转到Blank2Fragment
//            val action = NavigationNavigationDirections.actionGlobalBlank2Fragment()
//                .setIndex(22)
//            binding.navHostFragment.findNavController().navigate(action)
//        }
    }
}
//创建ViewModel
class NavigationViewModel() : ViewModel() {
    var text = "000"
}