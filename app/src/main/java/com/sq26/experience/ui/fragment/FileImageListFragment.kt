package com.sq26.experience.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.sq26.experience.R
import com.sq26.experience.adapter.CommonListAdapter
import com.sq26.experience.adapter.CommonListViewHolder
import com.sq26.experience.databinding.FragmentFileImageListBinding
import com.sq26.experience.databinding.ItemFileImageBinding
import com.sq26.experience.ui.activity.file.ImageInfo
import com.sq26.experience.ui.activity.file.ImageInfoCallback
import com.sq26.experience.viewmodel.FileImageViewModel

class FileImageListFragment : Fragment() {

    private val viewModel by activityViewModels<FileImageViewModel>()
    val args by navArgs<FileImageListFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFileImageListBinding.inflate(inflater, container, false).apply {
            val imageAdapter = object : CommonListAdapter<ImageInfo>(ImageInfoCallback) {
                override fun createView(
                    parent: ViewGroup,
                    viewType: Int
                ): CommonListViewHolder<*> {
                    return object : CommonListViewHolder<ItemFileImageBinding>(
                        ItemFileImageBinding.inflate(LayoutInflater.from(parent.context))
                    ) {
                        override fun bind(position: Int) {
                            v.image.setImageURI(getItem(position).uri.toString())
                        }
                    }
                }
            }
            recyclerView.adapter = imageAdapter
            viewModel.imageListFlow(args.index)
                .observe(viewLifecycleOwner) {
                    imageAdapter.submitList(it)
                    startPostponedEnterTransition()
                }
        }.root
    }

}