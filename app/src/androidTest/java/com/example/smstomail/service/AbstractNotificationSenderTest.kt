package com.example.smstomail.service

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.example.smstomail.domain.service.INotificationSender
import com.example.smstomail.domain.service.LegacyNotificationSender
import com.example.smstomail.domain.service.NotificationSender
import junit.framework.TestCase.assertNotNull


abstract class AbstractNotificationSenderTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val notificationSender: INotificationSender = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationSender(context)
    } else {
        LegacyNotificationSender(context)
    }
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



    fun notificationSend() {
        val notificationText = "notification${System.currentTimeMillis()}"
        notificationSender.showNotification(notificationText)

        Thread.sleep(2000)

        val testNotification = notificationManager.activeNotifications.find { notification ->
            notification.notification.extras.getString(Notification.EXTRA_TEXT) == notificationText
        }

        assertNotNull(testNotification)

        testNotification?.id?.let { notificationId ->
            notificationManager.cancel(notificationId)
        }
    }
}