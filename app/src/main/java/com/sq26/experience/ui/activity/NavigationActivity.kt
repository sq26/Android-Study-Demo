package com.sq26.experience.ui.activity

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.sq26.experience.NavNavigationGraphDirections
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityNavigationBinding
import com.sq26.experience.util.Log

//Navigation的基本使用
//目前发现Navigation只能往前跳转,和在fragment中向上返回,其他操作需要配合finish()
class NavigationActivity : AppCompatActivity() {
    //申明全局的viewModel
    private val viewModel: NavigationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_navigation)
        //获取navController
//        navController = Navigation.findNavController(this,R.id.nav_host_fragment)
//        navController = findNavController(R.id.nav_host_fragment)
//        navController = binding.navHostFragment.findNavController()
        findViewById<Button>(R.id.button1).setOnClickListener {
            //全局跳转到BlankFragment
            val directions =
                NavNavigationGraphDirections.actionGlobalBlank2Fragment()
            findNavController(R.id.nav_host).navigate(directions)
        }
    }
}

//创建ViewModel
class NavigationViewModel : ViewModel() {
    var text = "000"
}
//范围限定于导航图的 ViewModel
class GraphViewModel:ViewModel(){
    var text = MutableLiveData<String>()
}