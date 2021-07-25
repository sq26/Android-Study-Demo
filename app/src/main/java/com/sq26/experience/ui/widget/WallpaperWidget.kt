package com.sq26.experience.ui.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.datastore.preferences.core.edit
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.sq26.experience.R
import com.sq26.experience.datastore.ClockWidgetDataStoreKey
import com.sq26.experience.datastore.clockWidgetDataStore
import com.sq26.experience.util.i
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.min

/**
 * Implementation of App Widget functionality.
 */
class WallpaperWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach {
            updateAppWidget(context, it.toString())
        }
    }

    override fun onEnabled(context: Context) {
        "启用".i()
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        //删除微件id
        CoroutineScope(Dispatchers.IO).launch {
            context?.let {
                context.clockWidgetDataStore.edit {
                    val set =
                        it[ClockWidgetDataStoreKey.appWidgetIds]?.toMutableSet() ?: mutableSetOf()
                    appWidgetIds?.forEach { appWidgetId ->
                        set.remove(appWidgetId.toString())
                    }
                    it[ClockWidgetDataStoreKey.appWidgetIds] = set
                }
            }
        }
    }

    override fun onDisabled(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                0,
                Intent("com.sq26.experience.appwidget.ClockWidget").apply {
                    component = ComponentName(context, WallpaperWidget::class.java)
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        alarmManager.cancel(pendingIntent)
    }

}

internal fun updateAppWidget(
    context: Context,
    appWidgetId: String
) {
    CoroutineScope(Dispatchers.Main).launch {
        context.clockWidgetDataStore.data.first().let { data ->
            RemoteViews(context.packageName, R.layout.widget_wallpaper).also {

                AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId.toInt(), it)
            }
        }
    }


}