package com.sq26.experience.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionInflater
import com.sq26.experience.R
import com.sq26.experience.databinding.FragmentLanguageBinding

class LanguageFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        startPostponedEnterTransition()

        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.auto_transition)
//        sharedElementReturnTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentLanguageBinding.inflate(inflater, container, false).apply {

        }.root
    }


}