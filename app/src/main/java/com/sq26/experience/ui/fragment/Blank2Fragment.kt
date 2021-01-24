package com.sq26.experience.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sq26.experience.R
import com.sq26.experience.databinding.FragmentBlank2Binding

abstract class Blank2Fragment : Fragment() {
    private lateinit var binding: FragmentBlank2Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 设置此碎片的布局
        binding = FragmentBlank2Binding.inflate(inflater, container, false)
        return binding.root
    }

    private val args: Blank2FragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            text.text = args.index.toString()
            bottom1.setOnClickListener {
                val directions =
                    Blank2FragmentDirections.actionBlank2FragmentToBlankFragment(21)
                findNavController().navigate(directions)
            }
            bottom2.setOnClickListener {
                //获取指定页面的堆栈,并设置数据
                findNavController().getBackStackEntry(R.id.blankFragment).savedStateHandle["text"] =
                    "21"
                //返回到指定目标,destinationId是目标id,inclusive:是否连同目标一起移除,
                //如果目标页面是根页面会连同根页面一起移除,此时依然会返回到目标根页面,
                //但实际上页面已经不存在了在根页面的一切操作都会报错,
                //可以进行全局跳转,所以目标页是根目录时不要设置true
                findNavController().popBackStack(R.id.blankFragment, false)
            }
        }
    }
}