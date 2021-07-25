package com.sq26.experience.ui.activity

import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.RemoteViews
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.datastore.preferences.core.edit
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityWallpaperWidgetBinding
import com.sq26.experience.datastore.ClockWidgetDataStoreKey
import com.sq26.experience.datastore.clockWidgetDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class WallpaperWidgetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityWallpaperWidgetBinding>(
            this,
            R.layout.activity_wallpaper_widget
        ).apply {

            //1.首先，从启动该 Activity 的 Intent 获取应用微件 ID
            val appWidgetId = intent?.extras?.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
            //保存微件id
            CoroutineScope(Dispatchers.IO).launch {
                clockWidgetDataStore.edit {
                    val set =
                        it[ClockWidgetDataStoreKey.appWidgetIds]?.toMutableSet() ?: mutableSetOf()
                    set.add(appWidgetId.toString())
                    it[ClockWidgetDataStoreKey.appWidgetIds] = set
                }
            }
            //2.执行应用微件配置。

            //3.配置完成后，通过调用 getInstance(Context) 来获取 AppWidgetManager 的实例
            val appWidgetManager: AppWidgetManager =
                AppWidgetManager.getInstance(this@WallpaperWidgetActivity)

            toolbar.menu.add("保存").apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                setOnMenuItemClickListener {
                    onBackPressedDispatcher.onBackPressed()
                    true
                }
            }

            onBackPressedDispatcher.addCallback {
                //4.通过调用 updateAppWidget(int, RemoteViews) 来使用 RemoteViews 布局更新应用微件
                //保存微件文本颜色
                CoroutineScope(Dispatchers.Main).launch {
                    clockWidgetDataStore.edit {
//                        val jsonObject =
//                            if (it[ClockWidgetDataStoreKey.textColorMap] == null) {
//                                JSONObject()
//                            } else
//                                JSON.parseObject(it[ClockWidgetDataStoreKey.textColorMap])
//                        jsonObject[appWidgetId.toString()] = textColor
//                        it[ClockWidgetDataStoreKey.textColorMap] = jsonObject.toJSONString()
                        RemoteViews(packageName, R.layout.widget_wallpaper).also { views ->

                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        }
                    }
                    //5.最后，创建返回 Intent，为其设置 Activity 结果，然后结束该 Activity
                    val resultValue = Intent().apply {
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    }
                    setResult(RESULT_OK, resultValue)
                    finish()
                }
            }
        }
    }
}