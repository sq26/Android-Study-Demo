package com.sq26.experience.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sq26.experience.R
import com.sq26.experience.databinding.FragmentFileImageListBinding

class FileImageListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentFileImageListBinding>(
            inflater,
            R.layout.fragment_file_image_list, container, false
        ).root
    }

}