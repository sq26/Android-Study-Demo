package com.sq26.experience.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.sq26.experience.R
import com.sq26.experience.base.BaseViewModel
import com.sq26.experience.databinding.ActivityDataStoreBinding
import com.sq26.experience.util.Log
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class DataStoreActivity : AppCompatActivity() {
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityDataStoreBinding>(this, R.layout.activity_data_store)
            .apply {
                lifecycleOwner = this@DataStoreActivity
                viewModel = dataStoreViewModel
            }
    }
}

@HiltViewModel
class DataStoreViewModel @Inject constructor() : BaseViewModel() {

    fun show(view: View) {
        Log.i("测试")
        AlertDialog.Builder(view.context).setTitle("测试").show()
    }

//    fun save(view: View){
////       val view.context.createDataStore()
//    }
//
//    fun load(view: View){
//
//    }

}