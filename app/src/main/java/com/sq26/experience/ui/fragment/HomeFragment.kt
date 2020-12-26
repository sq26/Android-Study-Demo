package com.sq26.experience.ui.fragment

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.ui.viewinterop.viewModel
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.sq26.experience.R
import com.sq26.experience.databinding.FragmentHomeBinding
import com.sq26.experience.util.Log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = homeViewModel
            event = Event {
                Log.i("点击事件")
            }
//            requireActivity().onBackPressedDispatcher.addCallback(
//                viewLifecycleOwner,
//                !drawerLayout.isOpen
//            ) {
//                drawerLayout.closeDrawers()
//            }

            toolbar.setNavigationOnClickListener {
                drawerLayout.openDrawer(Gravity.LEFT)
            }
            findNavController().navigateUp(drawerLayout)
        }

        return binding.root
    }

    fun interface Event {
        fun onClick()
    }
}


class HomeViewModel : ViewModel() {
    val content = "主页"
    val left = "左边栏"
    val title = "首页"

}