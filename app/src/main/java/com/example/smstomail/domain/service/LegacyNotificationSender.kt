package com.example.smstomail.domain.service

import android.content.Context
import androidx.core.app.NotificationCompat

class LegacyNotificationSender(context: Context): AbstractNotificationSender(context) {
    override val notificationBuilder: NotificationCompat.Builder
        get() = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
}