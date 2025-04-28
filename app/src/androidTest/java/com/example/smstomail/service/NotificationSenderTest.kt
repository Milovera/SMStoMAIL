package com.example.smstomail.service

import android.Manifest
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.TIRAMISU) // android 13 require POST_NOTIFICATIONS permission
class NotificationSenderTest: AbstractNotificationSenderTest() {
    @Test
    fun testNotificationTest() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.apply {
            adoptShellPermissionIdentity()

            grantRuntimePermission(
                InstrumentationRegistry.getInstrumentation().targetContext.packageName,
                Manifest.permission.POST_NOTIFICATIONS
            )

            dropShellPermissionIdentity()
        }
        notificationSend()
    }
}