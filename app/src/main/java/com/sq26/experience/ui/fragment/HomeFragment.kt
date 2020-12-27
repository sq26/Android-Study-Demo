package com.sq26.experience.ui.fragment

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sq26.experience.R
import com.sq26.experience.data.HomeMenu
import com.sq26.experience.databinding.FragmentHomeBinding
import com.sq26.experience.databinding.ItemRecyclerviewBinding
import com.sq26.experience.util.Log
import com.sq26.experience.viewmodel.HomeViewModel
import com.sq26.experience.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Singleton

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel.isInit.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToStartFragment())
        }
        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = homeViewModel
            event = object : Event {
                override fun onClick() {
                    Log.i("点击事件")
                }
            }

            toolbar.setNavigationOnClickListener {
                drawerLayout.openDrawer(Gravity.LEFT)
            }
            val homeMenuAdapter = HomeMenuAdapter()
            menuRecyclerView.adapter = homeMenuAdapter
            subscribeUi(homeMenuAdapter)

            requireActivity().onBackPressedDispatcher.addCallback {
                isEnabled = !drawerLayout.isDrawerOpen(Gravity.LEFT).apply {
                    if (this)
                        drawerLayout.closeDrawers()
                }
            }

//            findNavController().navigateUp(drawerLayout)
        }

        return binding.root
    }

    private fun subscribeUi(homeMenuAdapter: HomeMenuAdapter) {
        homeViewModel.homeMenuList.observe(viewLifecycleOwner) {
            homeMenuAdapter.submitList(it)
        }
    }

    interface Event {
        fun onClick()
    }
}

class HomeMenuAdapter : ListAdapter<HomeMenu, HomeMenuAdapter.ViewHolder>(HomeMenuDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener {
                binding.homeMenu?.let {
                    Log.i("$it")
                }
            }
        }

        fun bind(item: HomeMenu) {
            binding.apply {
                homeMenu = item
                executePendingBindings()
            }
        }
    }
}

private class HomeMenuDiffCallback : DiffUtil.ItemCallback<HomeMenu>() {
    override fun areItemsTheSame(oldItem: HomeMenu, newItem: HomeMenu): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HomeMenu, newItem: HomeMenu): Boolean {
        return oldItem == newItem
    }
}

