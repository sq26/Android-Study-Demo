package com.sq26.experience.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonListAdapter
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.databinding.FragmentFileImageDirectoryBinding
import com.sq26.experience.databinding.ItemFileImageTypeBinding
import com.sq26.experience.databinding.LayoutFileImageHeaderBinding
import com.sq26.experience.ui.activity.file.FileImageListActivity
import com.sq26.experience.ui.activity.file.ImageType
import com.sq26.experience.ui.activity.file.ImageTypeCallback
import com.sq26.experience.util.Log
import com.sq26.experience.viewmodel.FileImageViewModel

class FileImageDirectoryFragment : Fragment() {
    private val viewModel: FileImageViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DataBindingUtil.inflate<FragmentFileImageDirectoryBinding>(
            inflater,
            R.layout.fragment_file_image_directory,
            container,
            false
        ).apply {

            val adapter = object : CommonListAdapter<ImageType>(ImageTypeCallback) {
                override fun createView(
                    parent: ViewGroup,
                    viewType: Int
                ): CommonListViewHolder<*> {
                    return if (viewType == 0) {
                        object : CommonListViewHolder<LayoutFileImageHeaderBinding>(
                            LayoutFileImageHeaderBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
                        ) {
                            override fun bind(position: Int) {
                                Log.i(position, "he")
                            }
                        }
                    } else {
                        object : CommonListViewHolder<ItemFileImageTypeBinding>(
                            ItemFileImageTypeBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
                        ) {

                            override fun bind(position: Int) {
                                Log.i(position, "body")
                                val item = getItem(position)
                                v.item = item
                                executePendingBindings()
                                v.image.setImageURI(item.uri.toString())
                                v.setOnClick {

                                    findNavController().navigate(
                                        FileImageDirectoryFragmentDirections.actionFileImageDirectoryFragmentToFileImageListFragment(position)
                                    )
                                }
                            }
                        }
                    }
                }

                override fun getItemCount(): Int {
                    return super.getItemCount() + 1
                }

                override fun getItem(position: Int): ImageType {
                    return super.getItem(position - 1)
                }

                override fun getItemViewType(position: Int): Int {
                    return if (position == 0) 0 else 1
                }
            }
            recyclerView.layoutManager =
                GridLayoutManager(requireActivity(), 2).also {
                    it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == 0)
                                it.spanCount
                            else
                                1
                        }

                    }
                }
            recyclerView.adapter = adapter
            viewModel.imagesTypeLiveData.observe(requireActivity()) {
                adapter.submitList(it)
            }
        }.root
    }
}