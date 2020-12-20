package com.sq26.experience.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.sq26.experience.R
import com.sq26.experience.databinding.FragmentBlank2Binding
import com.sq26.experience.databinding.FragmentBlankBinding

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * 创建此片段的一个实例。
 */
class BlankFragment : Fragment() {
    //公用属性
    private var mParam1: String? = null
    private var mParam2: String? = null

    companion object {
        // 片段初始化参数，例如ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * 使用此工厂方法创建一个新的实例
         * 该片段使用提供的参数。
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return 片段BlankFragment的新实例。
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): BlankFragment =
        //通过setArguments的方式保存的参数不会应为屏幕旋转或是其他重新创建的情况而丢失
            //apply用于对象的初始化时对其属性赋值,返回值是调用者自身
            BlankFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //恢复参数
        //使用let方法做非空判断,arguments不等空就执行函数体中的代码
        //it表示不为空的arguments
        arguments?.let {
            mParam1 = it.getString(ARG_PARAM1)
            mParam2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentBlankBinding? = null

    // 此属性仅在onCreateView和onDestroyView之间有效
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 创建此片段的布局
        _binding = FragmentBlankBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.text.text = "1"
        binding.bottom.setOnClickListener {
            val action = BlankFragmentDirections.actionBlankFragmentToBlank2Fragment()
                .setIndex(22)
            findNavController().navigate(action)
        }
    }

    private fun start() {
        //指定跳转行为,进行跳转
//        navController.navigate(R.id.action_blankFragment_to_blank2Fragment);
    }
}