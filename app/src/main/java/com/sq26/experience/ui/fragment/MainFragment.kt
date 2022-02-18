package com.sq26.experience.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonListAdapter
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.databinding.FragmentMainBinding
import com.sq26.experience.databinding.ItemRecyclerviewBinding
import com.sq26.experience.entity.HomeMenu
import com.sq26.experience.entity.HomeMenuDiffCallback
import com.sq26.experience.util.i


class MainFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentMainBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner

            val adapter = object : CommonListAdapter<HomeMenu>(HomeMenuDiffCallback()) {
                override fun createView(parent: ViewGroup, viewType: Int): CommonListViewHolder<*> {
                    return object : CommonListViewHolder<ItemRecyclerviewBinding>(
                        ItemRecyclerviewBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    ) {
                        init {
                            v.setClickListener {
//                                it.transitionName.i("it")
                                v.homeMenu?.let { item ->
                                    //设置要跳转的页面
                                    when (item.id) {
                                        "Moshi" -> {
                                            findNavController().navigate(MainFragmentDirections.actionMainFragmentToMoshiFragment())
                                        }
                                        "Language" -> {
                                            val direction =
                                                MainFragmentDirections.actionMainFragmentToLanguageFragment()
                                            findNavController().navigate(
                                                direction,
                                                FragmentNavigatorExtras(it to "main")
                                            )
                                        }
                                        "FloatingActionButtonLayout" -> findNavController().navigate(
                                            MainFragmentDirections.actionMainFragmentToFloatingActionButtonLayoutFragment()
                                        )
                                        "PermissionRequest" -> findNavController().navigate(
                                            MainFragmentDirections.actionMainFragmentToPermissionRequestFragment()
                                        )

                                    }

                                }
                            }
                        }

                        override fun bind(position: Int) {
                            v.homeMenu = getItem(position)
                        }
                    }
                }

            }

            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            recyclerView.adapter = adapter

            lifecycleScope.launchWhenStarted {
                postponeEnterTransition()
                adapter.submitList(
                    listOf(
                        HomeMenu("Moshi", "Moshi Json解析框架", 1),
                        HomeMenu("Language", "多语言适配", 1),
                        HomeMenu(
                            "FloatingActionButtonLayout",
                            "自定义布局:FloatingActionButtonLayout",
                            1
                        ),
                        HomeMenu(
                            "PermissionRequest",
                            "权限申请",
                            1
                        )
                    )
                )

                (root.parent as? ViewGroup)?.doOnPreDraw {
                    startPostponedEnterTransition()
                }
            }


        }.root
    }

}