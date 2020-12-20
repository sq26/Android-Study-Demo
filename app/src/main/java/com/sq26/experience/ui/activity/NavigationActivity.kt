package com.sq26.experience.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.sq26.experience.NavigationNavigationDirections
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityNavigationBinding
import com.sq26.experience.ui.fragment.Blank2FragmentDirections
import com.sq26.experience.ui.fragment.BlankFragmentDirections

//Navigation的基本使用
class NavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavigationBinding
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

        //声明NavController,整个 Navigation 架构中 最重要的核心类，我们所有的导航行为都由 NavController 处理
        //宿主activity提前申明获取navController的方法
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        var  navController = navHostFragment.navController

        binding.button1.setOnClickListener {
            //binding.navHostFragment.findNavController()在视图没有显示之前调用会报下标越界错误
            //全局跳转到BlankFragment
            val action = NavigationNavigationDirections.actionGlobalBlankFragment()
            binding.navHostFragment.findNavController().navigate(action)
        }
        binding.button2.setOnClickListener {
            //全局跳转到BlankFragment
            val action = NavigationNavigationDirections.actionGlobalBlank2Fragment()
                .setIndex(2)
            binding.navHostFragment.findNavController().navigate(action)
        }
    }

//    @OnClick(R.id.button1, R.id.button2)
//    fun onViewClicked(view: View) {
//        when (view.id) {
//            R.id.button1 ->
//
//
//                //跳转到blankFragment
//                navController.navigate(R.id.blankFragment)
//            R.id.button2 ->                 //跳转到blank2Fragment
//                navController.navigate(R.id.blank2Fragment)
//        }
//    }

//    override fun onSupportNavigateUp(): Boolean {
//        //接管activity的返回
//        return navController.navigateUp()
//    }
}