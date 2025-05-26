package com.example.smstomail.domain.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

@RequiresApi(Build.VERSION_CODES.O)
class NotificationSender(context: Context): AbstractNotificationSender(context) {
    companion object {
        fun createNotificationChannel(context: Context, channelName: String, channelDescription: String) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = channelDescription

            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override val notificationBuilder: NotificationCompat.Builder
        get() = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
}