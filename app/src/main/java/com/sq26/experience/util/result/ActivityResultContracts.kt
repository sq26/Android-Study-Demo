package com.sq26.experience.util.result

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sq26.experience.util.permissions.PermissionsFragment

class StartActivityForResult(private val fragmentManager: FragmentManager) {
    private lateinit var fragment: StartActivityFragment
    fun launch(intent: Intent, callback: (ActivityResult) -> Unit) {

        fragment = StartActivityFragment(intent) {
            removeFragment()
            callback(it)
        }
        //获取FragmentManager
        val fragmentTransaction =
            fragmentManager.beginTransaction()
        //将fragment加入到activity中
        fragmentTransaction.add(fragment, "startActivityFragment")
        //提交Fragment进行申请
        fragmentTransaction.commit()
    }

    //移除请求权限的fragment
    private fun removeFragment() {
        val fragmentTransaction =
            fragmentManager.beginTransaction()
        //将fragment从activity中移除
        fragmentTransaction.remove(fragment)
        //提交
        fragmentTransaction.commit()
    }
}

class StartActivityFragment(private val intent: Intent, callback: (ActivityResult) -> Unit) :
    Fragment() {

    private val request =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            callback(it)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        request.launch(intent)
    }
}

class OpenDocumentTree(private val fragmentManager: FragmentManager) {
    private lateinit var fragment: OpenDocumentTreeFragment

    //uri是指定需要设置目录的uri,设置null表示不指定打开的目录
    fun launch(uri: Uri? = null, callback: (Uri?) -> Unit) {
        fragment = OpenDocumentTreeFragment(uri) {
            removeFragment()
            callback(it)
        }
        //获取FragmentManager
        val fragmentTransaction =
            fragmentManager.beginTransaction()
        //将fragment加入到activity中
        fragmentTransaction.add(fragment, "openDocumentTreeFragment")
        //提交Fragment进行申请
        fragmentTransaction.commit()
    }

    //移除请求权限的fragment
    private fun removeFragment() {
        val fragmentTransaction =
            fragmentManager.beginTransaction()
        //将fragment从activity中移除
        fragmentTransaction.remove(fragment)
        //提交
        fragmentTransaction.commit()
    }
}

class OpenDocumentTreeFragment(private val uri: Uri? = null, callback: (Uri?) -> Unit) :
    Fragment() {

    private val request =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
            it?.let {
                requireActivity().contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            callback(it)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        request.launch(uri)
    }
}