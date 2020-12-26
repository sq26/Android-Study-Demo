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
import com.sq26.experience.util.Log

// TODO: Rename parameter arguments, choose names that match
// 片段初始化参数，例如ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Blank2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Blank2Fragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private var _binding: FragmentBlank2Binding? = null

    // 此属性仅在onCreateView和onDestroyView之间有效
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 设置此碎片的布局
        _binding = FragmentBlank2Binding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val args: Blank2FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(args.index.toString(), "index")
        binding.text.text = args.index.toString()
        binding.bottom.setOnClickListener {
            val action = Blank2FragmentDirections.actionBlank2FragmentToBlankFragment()
            findNavController().navigate(action)
        }
        binding.bottom2.setOnClickListener {
//            findNavController()
            findNavController().navigateUp()
        }
    }
}