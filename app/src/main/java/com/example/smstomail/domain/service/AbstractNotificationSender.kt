package com.example.smstomail.domain.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smstomail.R
import kotlin.random.Random

abstract class AbstractNotificationSender(protected val context: Context): INotificationSender {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "435"
    }

    abstract val notificationBuilder: NotificationCompat.Builder

    override fun showNotification(message: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val notification = notificationBuilder
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setLargeIcon(Icon.createWithResource(context, R.mipmap.ic_launcher_foreground))
            .setContentTitle(context.getText(R.string.app_name))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(false)
            .build()

        NotificationManagerCompat.from(context).notify(Random.nextInt(), notification)
    }
}