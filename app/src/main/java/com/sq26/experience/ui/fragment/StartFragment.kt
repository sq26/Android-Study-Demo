package com.sq26.experience.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sq26.experience.databinding.FragmentStartBinding
import com.sq26.experience.util.setOnClickAntiShake
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class StartFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentStartBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            val job = lifecycleScope.launch {
                delay(3000)
                start.performClick()
            }
            start.setOnClickAntiShake {
                job.cancel()
                findNavController().navigate(StartFragmentDirections.actionStartFragmentToMainFragment())
            }



        }.root
    }
}