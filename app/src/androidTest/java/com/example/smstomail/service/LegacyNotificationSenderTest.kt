package com.example.smstomail.service

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(maxSdkVersion = Build.VERSION_CODES.S_V2) // < Android 12.1
class LegacyNotificationSenderTest: AbstractNotificationSenderTest() {
    @Test
    fun testNotificationTest() {
        notificationSend()
    }
}