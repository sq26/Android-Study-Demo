package com.sq26.experience.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityStatusBarBinding
import com.sq26.experience.util.Log
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class StatusBarActivity : AppCompatActivity() {
    private val statusBarViewModel: StatusBarViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityStatusBarBinding>(this, R.layout.activity_status_bar)
            .apply {
                lifecycleOwner = this@StatusBarActivity
                viewModel = statusBarViewModel
                isInsetsController = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

                toolbar.setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
            }

        statusBarViewModel.change.observe(this) {
            Log.i("变化")
            Log.i(statusBarViewModel.SYSTEM_UI_FLAG_LOW_PROFILE, "变化")
            val decorView = window.decorView;

            var uiOptions = 0;
            if (statusBarViewModel.SYSTEM_UI_FLAG_LOW_PROFILE) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }
            if (statusBarViewModel.SYSTEM_UI_FLAG_HIDE_NAVIGATION) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if (statusBarViewModel.SYSTEM_UI_FLAG_FULLSCREEN) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
            if (statusBarViewModel.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            }
            if (statusBarViewModel.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            }
            if (statusBarViewModel.SYSTEM_UI_FLAG_LAYOUT_STABLE) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            }
            if (statusBarViewModel.SYSTEM_UI_FLAG_IMMERSIVE) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE;
            }
            if (statusBarViewModel.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            decorView.systemUiVisibility = uiOptions;
        }

    }
}

@HiltViewModel
class StatusBarViewModel @Inject constructor(
) : ViewModel() {

    private val _change = MutableLiveData<Boolean>()
    val change: LiveData<Boolean> = _change

    //弱化状态栏和导航栏的图标(设置状态栏和导航栏中的图标变小，变模糊或者弱化其效果。这个标志一般用于游戏，电子书，视频，或者不需要去分散用户注意力的应用软件)
    var SYSTEM_UI_FLAG_LOW_PROFILE = false
        set(value) {
            field = value
            _change.postValue(field)
        }

    //隐藏导航栏，点击屏幕任意区域，导航栏将重新出现，并且不会自动消失
    var SYSTEM_UI_FLAG_HIDE_NAVIGATION = false
        set(value) {
            field = value
            _change.postValue(field)
        }

    //隐藏状态栏(点击屏幕区域不会出现，需要从状态栏位置下拉才会出现,并且不会自动消失)
    var SYSTEM_UI_FLAG_FULLSCREEN = false
        set(value) {
            field = value
            _change.postValue(field)
        }

    //拓展布局到导航栏后面(将布局内容拓展到导航栏的后面)
    var SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION = false
        set(value) {
            field = value
            _change.postValue(field)
        }

    //拓展布局到状态栏后面(将布局内容拓展到状态的后面)
    var SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = false
        set(value) {
            field = value
            _change.postValue(field)
        }

    //稳定的布局，不会随系统栏的隐藏、显示而变化(主要是在全屏和非全屏切换时，布局不要有大的变化。一般和隐藏导航栏、隐藏状态栏搭配使用)
    var SYSTEM_UI_FLAG_LAYOUT_STABLE = false
        set(value) {
            field = value
            _change.postValue(field)
        }

    //沉浸模式，用户可以交互的界面(使状态栏和导航栏真正的进入沉浸模式,即全屏模式，如果没有设置这个标志，设置全屏时，我们点击屏幕的任意位置，就会恢复为正常模式,一般和隐藏导航栏、隐藏状态栏搭配使用)
    var SYSTEM_UI_FLAG_IMMERSIVE = false
        set(value) {
            field = value
            _change.postValue(field)
        }

    //沉浸模式，用户可以交互的界面。同时，用户上下拉系统栏时，会自动隐藏系统栏(它的效果跟View.SYSTEM_UI_FLAG_IMMERSIVE一样。但是，它在全屏模式下，用户上下拉状态栏或者导航栏时，这些系统栏只是以半透明的状态显示出来，并且在一定时间后会自动消失)
    var SYSTEM_UI_FLAG_IMMERSIVE_STICKY = false
        set(value) {
            field = value
            _change.postValue(field)
        }

    fun showStatusBar(view: CompoundButton, isChecked: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //设置侵入式体验
            view.windowInsetsController?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (isChecked)
                view.windowInsetsController?.show(WindowInsetsCompat.Type.statusBars())
            else
                view.windowInsetsController?.hide(WindowInsetsCompat.Type.statusBars())

//            view.windowInsetsController?.systemBarsBehavior =
//                WindowInsetsController.BEHAVIOR_DEFAULT
        }
    }

    fun showNavigationBar(view: CompoundButton, isChecked: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //设置侵入式体验
            view.windowInsetsController?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (isChecked)
                view.windowInsetsController?.show(WindowInsetsCompat.Type.navigationBars())
            else
                view.windowInsetsController?.hide(WindowInsetsCompat.Type.navigationBars())

//            view.windowInsetsController?.systemBarsBehavior =
//                WindowInsetsController.BEHAVIOR_DEFAULT
        }
    }


    fun showSystemBar(view: CompoundButton, isChecked: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //设置侵入式体验
            view.windowInsetsController?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (isChecked)
                view.windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
            else
                view.windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())

//            view.windowInsetsController?.systemBarsBehavior =
//                WindowInsetsController.BEHAVIOR_DEFAULT
        }
    }

    fun showTest(view: CompoundButton, isChecked: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //设置侵入式体验
            view.windowInsetsController?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (isChecked)
                view.windowInsetsController?.show(WindowInsetsCompat.Type.captionBar())
            else
                view.windowInsetsController?.hide(WindowInsetsCompat.Type.captionBar())

//            view.windowInsetsController?.systemBarsBehavior =
//                WindowInsetsController.BEHAVIOR_DEFAULT
        }
    }
}