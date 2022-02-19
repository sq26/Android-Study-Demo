package com.sq26.experience.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import com.sq26.experience.databinding.FragmentFloatingActionButtonLayoutBinding
import com.sq26.experience.util.kotlin.toast
import com.sq26.experience.util.setOnClickAntiShake
import kotlin.math.max


class FloatingActionButtonLayoutFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFloatingActionButtonLayoutBinding.inflate(inflater, container, false).apply {

            button.setOnClickAntiShake {
                text.isVisible = !text.isVisible
            }

            button2.setOnClickListener {
                val view = Button(requireContext())
                view.text = menu.contextCount().toString()
                view.setOnClickListener {
                    requireContext().toast(view.text)
                }
                menu.addView(view)
            }

            button3.setOnClickListener {
                menu.removeViewAt(max(menu.contextCount() - 1, 0))
            }

        }.root
    }

}