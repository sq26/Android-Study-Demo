package com.sq26.experience.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.sq26.experience.R
import com.sq26.experience.databinding.FragmentPermissionRequestBinding
import com.sq26.experience.util.kotlin.toast
import com.sq26.experience.util.setOnClickAntiShake
import com.sq26.experience.viewmodel.PermissionRequestViewModel

class PermissionRequestFragment : Fragment() {
    private val viewModel by viewModels<PermissionRequestViewModel>()


    private val req = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK){
            requireContext().toast("已获取权限")
        }else{
            requireContext().toast("未获取权限")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentPermissionRequestBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            main = viewModel

            request.setOnClickAntiShake {
                viewModel.startPermissions(requireActivity())
            }

            requestFile.setOnClickAntiShake {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                    if (Environment.isExternalStorageManager()) {
                        requireContext().toast("已获取权限")
                    } else {
                        req.launch(Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
                    }

                } else {
                    requireContext().toast("Android10及一下直接申请储存权限")
                }


            }
        }.root
    }

}