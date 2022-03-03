package com.sq26.experience.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedDispatcher
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sq26.experience.R
import com.sq26.experience.app.AppDataStoreKey
import com.sq26.experience.app.dataStore
import com.sq26.experience.databinding.FragmentLanguageBinding
import com.sq26.experience.util.setOnClickAntiShake
import kotlinx.coroutines.launch

class LanguageFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        startPostponedEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.auto_transition)
//        sharedElementReturnTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentLanguageBinding.inflate(inflater, container, false).apply {

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            button.setOnClickAntiShake {
                val values = LanguageEnum.values()
                MaterialAlertDialogBuilder(requireActivity())
                    .setItems(values.map { it.language }.toTypedArray()) { _, i ->
                        lifecycleScope.launch {
                            requireContext().dataStore.edit {
                                //修改选中的语言
                                fun update() {
                                    //设置选中的语言
                                    it[AppDataStoreKey.language] = values[i].value
                                    //重新加载当前视图
                                    requireActivity().recreate()
                                }
                                if (it[AppDataStoreKey.language].isNullOrEmpty() &&
                                    values[i].value != LanguageEnum.Chinese.value
                                ) {
                                    update()
                                } else if (it[AppDataStoreKey.language] != values[i].value) {
                                    update()
                                }
                            }
                        }
                    }.show()
            }

            text.text = getString(R.string.language)

        }.root
    }
}

//语言枚举类
enum class LanguageEnum(val language: String, val value: String) {
    Chinese("简体中文", "zh"),
    English("English", "en")
}