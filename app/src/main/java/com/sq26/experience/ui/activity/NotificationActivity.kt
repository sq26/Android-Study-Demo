package com.sq26.experience.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import com.sq26.experience.R
import com.sq26.experience.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityNotificationBinding>(
            this,
            R.layout.activity_notification
        ).apply {
            lifecycleOwner = this@NotificationActivity

            //获取通知服务
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channelId = "demo"
                val channelName = "测试"

                /**
                 * 通知权限, 数值越高，提示权限就越高
                 * IMPORTANCE_DEFAULT= 3;
                 * IMPORTANCE_HIGH = 4;
                 * IMPORTANCE_LOW = 2;
                 * IMPORTANCE_MAX = 5;
                 * IMPORTANCE_MIN = 1;
                 * IMPORTANCE_NONE = 0;
                 * IMPORTANCE_UNSPECIFIED = -1000;
                 */
                val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    //是否开启指示灯（是否在桌面icon右上角展示小红点）
                    enableLights(true)
                    //设置指示灯颜色
                    lightColor = Color.RED
                    //是否开启震动
                    enableVibration(true)
                    //设置震动频率
                    vibrationPattern = longArrayOf(1)
                    //设置绕过免打扰模式
                    setBypassDnd(true)
                    //设置是否应在锁定屏幕上显示此频道的通知
//                    lockscreenVisibility =
                    //设置是否显示角标
                    setShowBadge(true)
                }


                //创建通知渠道
                notificationManager.createNotificationChannel(notificationChannel)
            }


            notification1.setOnClickListener {
                //创建通知
                val notification = NotificationCompat.Builder(this@NotificationActivity,"demo")
                    //设置点击通知后自动清除通知
                    .setAutoCancel(false)
                    //设置通知的标题内容
                    .setContentTitle("标题")
                    //设置通知的正文内容
                    .setContentText("正文")
                    //设置点击通知后的跳转意图
//                    .setContentIntent()
                    //设置通知被创建的时间
                    .setWhen(0)
                    //设置通知的小图标
                    .setSmallIcon(R.drawable.ic_baseline_add_24)
                    //设置通知的大图标
//                    .setLargeIcon()
                    /**设置通知的重要程度
                     * PRIORITY_DEFAULT:默认,不重要
                     * PRIORITY_MIN:最低的重要程度，系统可能只会在特定的场合显示这条通知
                     * PRIORITY_LOW:较低的重要程度，系统可能会将这类通知缩小，或改变其显示的顺序
                     * PRIORITY_HIGH:较高的重要程度，系统可能会将这类通知放大，或改变其显示的顺序
                     * PRIORITY_MAX:最高的重要程度，表示这类通知消息必须让用户看到，甚至做出响应
                     */
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()


                //显示通知
                notificationManager.notify(1, notification)

            }

        }
    }
}