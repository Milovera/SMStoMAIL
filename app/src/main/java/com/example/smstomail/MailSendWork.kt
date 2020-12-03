package com.example.smstomail

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import java.lang.Exception
import javax.mail.MessagingException
import javax.mail.internet.AddressException
import kotlin.random.Random

class MailSendWork(context: Context, params: WorkerParameters): Worker(context, params) {
    companion object {
        const val LOG_TAG = "MailSendWork"
        const val NOTIFICATION_CHANNEL_ID = "435"

        var NOTIFICATION_CHANNEL: NotificationChannel? = null

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .setRequiresStorageNotLow(false)
            .setRequiresDeviceIdle(false)
            .build()

        fun scheduleWork(context: Context, mailTitle: String, mailBody: String) {
            val workRequest = OneTimeWorkRequestBuilder<MailSendWork>()
                .setInputData(workDataOf(PreferencesKeys.MAIL_TITLE_KEY to mailTitle,
                    PreferencesKeys.MAIL_BODY_KEY to mailBody))
                .setConstraints(MailSendWork.constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueue(workRequest)
        }
    }

    override fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences(PreferencesKeys.PREFERENCES_KEY, Context.MODE_PRIVATE)

        val emailLogin: String = sharedPreferences.getString(PreferencesKeys.LOGIN_KEY, "")!!
        val emailPassw: String = sharedPreferences.getString(PreferencesKeys.PASSW_KEY, "")!!
        val emailRecipients: String = sharedPreferences.getString(PreferencesKeys.RECIPIENTS_KEY, "")!!
        val emailHost: String = sharedPreferences.getString(PreferencesKeys.SERVER_KEY, "")!!
        val sslPort: String = sharedPreferences.getString(PreferencesKeys.SSL_PORT_KEY, "")!!
        val emailTitle: String = inputData.getString(PreferencesKeys.MAIL_TITLE_KEY) ?: ""
        val emailBody: String = inputData.getString(PreferencesKeys.MAIL_BODY_KEY) ?: ""

        Log.v(LOG_TAG, "Start work with Title: $emailTitle\nBody:\n$emailBody")

        val emailRecipientsArr = emailRecipients.split(",")

        if(emailLogin.isEmpty() || emailPassw.isEmpty() || emailRecipientsArr.isEmpty() || emailHost.isEmpty() || sslPort.isEmpty() ||
            (emailTitle.isEmpty() && emailBody.isEmpty())) {
                Log.d(LOG_TAG, "From: $emailLogin\nTo: $emailRecipients\nHost: $emailHost:$sslPort")
                return Result.failure()
        }

        val sender = SMTPMailSender(emailHost, sslPort, EmailAuthenticator(emailLogin, emailPassw))

        for (recipientStr in emailRecipientsArr) {
            val recipient = recipientStr.trim()

            if (recipient.isNotEmpty()) {
                try {
                    sender.doSend(recipient, emailTitle, emailBody)
                    Log.i(SMTPMailSender.LOG_TAG, "Mail to $recipient successfully send")
                }
                catch (ex: Exception) {
                    Log.d(SMTPMailSender.LOG_TAG, "EmailFrom: ${emailLogin}\nEmailTo: $recipient")
                    Log.e(SMTPMailSender.LOG_TAG, ex.toString())
                    val exMessage = ex.message

                    if (exMessage != null) {
                        if(exMessage.contains("Invalid Addresses")) {
                            showNotification(applicationContext.getString(R.string.error_while_mail_sending, recipient))
                            continue
                        } else if (exMessage.contains("Authentication failed")) {
                            showNotification(applicationContext.getString(R.string.error_while_mail_sending3))
                            return Result.failure()
                        }
                    }

                    showNotification(applicationContext.getString(R.string.error_while_mail_sending2, recipient))
                }
            }
        }

        return Result.success()
    }

    private fun showNotification(text: String) {
        if(NOTIFICATION_CHANNEL == null) {
            val newChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "ErrChannel", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Channel for displaying errors while mail sending"
            }
            NOTIFICATION_CHANNEL = newChannel

            val notificationManager: NotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(newChannel)
        }

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(applicationContext.getText(R.string.app_name))
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(false)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(Random.nextInt(), notification)
    }
}