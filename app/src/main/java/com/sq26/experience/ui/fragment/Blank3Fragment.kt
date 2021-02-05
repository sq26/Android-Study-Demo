package com.sq26.experience.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sq26.experience.R
import com.sq26.experience.databinding.FragmentBlank3Binding
import com.sq26.experience.ui.activity.GraphViewModel

abstract class Blank3Fragment : Fragment() {
    lateinit var binding: FragmentBlank3Binding
    //获取范围限定于导航图的 ViewModel
    private val mGraphViewModel: GraphViewModel by navGraphViewModels(R.id.nav_navigation_graph)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlank3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            text2.text = mGraphViewModel.text.value
            button1.setOnClickListener {
                val directions =
                    Blank3FragmentDirections.actionBlank3FragmentToBlank2Fragment(32)
                findNavController().navigate(directions)
            }
            button2.setOnClickListener {
                findNavController().previousBackStackEntry!!.savedStateHandle["text"] = "31"
                findNavController().navigateUp()
            }
            button3.setOnClickListener {
                //创建显式深层链接
                val pendingIntent = findNavController().createDeepLink()
                    .setDestination(R.id.blank4Fragment)
                    .createPendingIntent()
                //执行跳转,跳转后导航的返回堆栈会被清除,因此从blank4Fragment返回时会直接到达blankFragment
                //而不是blank3Fragment
                pendingIntent.send()
            }
            button4.setOnClickListener {
                //创建隐式深层连接
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sq26://blank.fragment4/"))
                intent.putExtra("index",34)
                //添加此flag返回堆栈会被清除,因此从blank4Fragment返回时会直接到达blankFragment
                //不加此flag可以正常返回
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }
}