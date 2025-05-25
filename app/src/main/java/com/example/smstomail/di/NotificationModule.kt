package com.example.smstomail.di

import android.content.Context
import android.os.Build
import com.example.smstomail.R
import com.example.smstomail.domain.service.INotificationSender
import com.example.smstomail.domain.service.LegacyNotificationSender
import com.example.smstomail.domain.service.NotificationSender
import dagger.Module
import dagger.Provides

@Module
class NotificationModule {
    @Provides
    fun notificationSender(context: Context): INotificationSender {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationSender.Companion.createNotificationChannel(
                context = context,
                channelName = context.getString(R.string.notification_channel_name),
                channelDescription = context.getString(R.string.notification_channel_description)
            )
            NotificationSender(context)
        } else {
            LegacyNotificationSender(context)
        }
    }
}