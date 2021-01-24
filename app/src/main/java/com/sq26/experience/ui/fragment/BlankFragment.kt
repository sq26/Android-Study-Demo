package com.sq26.experience.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.sq26.experience.R
import com.sq26.experience.databinding.FragmentBlankBinding
import com.sq26.experience.ui.activity.GraphViewModel
import com.sq26.experience.ui.activity.NavigationViewModel
import com.sq26.experience.util.Log

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * 创建此片段的一个实例。
 */
abstract class BlankFragment : Fragment() {
    private val viewModel: BlankViewModel by viewModels()

    //获取宿主activity的viewModel
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    //获取范围限定于导航图的 ViewModel
    private val mGraphViewModel: GraphViewModel by navGraphViewModels(R.id.nav_navigation_graph)

    //获取传入的参数
    private val args: BlankFragmentArgs by navArgs()

    //onCreate只会创建一次
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("BlankFragment创建")
        //获取当前堆栈的保存状态
        val currentBackStackEntry = findNavController().currentBackStackEntry!!
        val savedStateHandle = currentBackStackEntry.savedStateHandle
        savedStateHandle.getLiveData<String>("text").observe(currentBackStackEntry) {
            viewModel.text2 = it
        }
        //添加返回监听,默认是启用状态
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            Toast.makeText(activity, "再点一次退出", Toast.LENGTH_SHORT).show()
            //设置不起用下次点击返回才会返回上级界面
            isEnabled = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i("BlankFragment创建视图")
        // 创建此片段的布局
        val binding = DataBindingUtil.inflate<FragmentBlankBinding>(
            inflater,
            R.layout.fragment_blank,
            container,
            false
        ).apply {
            blankViewModel = viewModel
            graphViewModel = mGraphViewModel
            //设置dataBinding生命周期
            lifecycleOwner = viewLifecycleOwner
            bottom1OnClick = View.OnClickListener {
                val directions =
                    BlankFragmentDirections.actionBlankFragmentToBlank3Fragment(13)
                findNavController().navigate(directions)
            }
            bottom2OnClick = View.OnClickListener {
                val directions =
                    BlankFragmentDirections.actionBlankFragmentToNavigationDemoActivity(10)
                findNavController().navigate(directions)
            }
            //简写点击事件
            setBottom3OnClick {
                //向上返回,已经到顶就不会返回了,会返回成功或失败
//                findNavController().navigateUp()
                //向上返回,这个可以指定要返回的页面
                mGraphViewModel.text.postValue("11")
//                findNavController().popBackStack()
            }
            setBottom4OnClick {
                //创建隐式深层连接
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sq26://blank.fragment4/"))
                intent.putExtra("index", 14)
                //不加此flag可以正常返回
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            viewModel.text = args.index.toString()
        }
        return binding.root
    }

    //onViewCreated被遮挡后显示会再次调用
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

}

class BlankViewModel : ViewModel() {
    var text = ""
    var text2 = ""
}