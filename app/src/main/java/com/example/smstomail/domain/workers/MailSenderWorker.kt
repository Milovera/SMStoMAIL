package com.example.smstomail.domain.workers

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.smstomail.R
import com.example.smstomail.data.entity.Message
import com.example.smstomail.data.entity.Recipient
import com.example.smstomail.data.repository.ISettingsRepository
import com.example.smstomail.domain.service.IMessageSender
import com.example.smstomail.domain.service.INotificationSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MailSenderWorker(
    context: Context, params: WorkerParameters,
    private val notificationSender: INotificationSender,
    private val mailSender: IMessageSender,
    settingsRepository: ISettingsRepository
): CoroutineWorker(context, params) {
    enum class WorkDataKeys{
        MAIL_TILE_KEY,
        MAIL_BODY_KEY,
        MAIL_RECIPIENT
    }
    companion object {
        const val LOG_TAG = "MailSendWork"

        val DEFAULT_CONSTRAINTS by lazy {
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        }

        fun createRequest(message: Message, recipient: String, constraints: Constraints = DEFAULT_CONSTRAINTS): WorkRequest {
            return OneTimeWorkRequest.Builder(MailSenderWorker::class.java)
                .setInputData(
                    buildWorkerParameters(
                        message = message,
                        recipient = recipient
                    )
                )
                .setConstraints(constraints)
                .build()
        }
        fun buildWorkerParameters(message: Message, recipient: String): Data {
            return workDataOf(
                WorkDataKeys.MAIL_TILE_KEY.name to message.sender,
                WorkDataKeys.MAIL_BODY_KEY.name to message.body,
                WorkDataKeys.MAIL_RECIPIENT.name to recipient
            )
        }
    }

    private val mailTitle = inputData.getString(WorkDataKeys.MAIL_TILE_KEY.name)
    private val mailBody = inputData.getString(WorkDataKeys.MAIL_BODY_KEY.name)
    private val recipient = inputData.getString(WorkDataKeys.MAIL_RECIPIENT.name)
    private val settings = settingsRepository.read()

    override suspend fun doWork(): Result {
        if (mailTitle == null || mailTitle.isEmpty() || mailBody == null || mailBody.isEmpty() ||
            recipient == null || recipient.isEmpty() || settings == null) {
            return Result.failure()
        }

        val message = Message(sender = mailTitle, body = mailBody)

        Log.d(LOG_TAG, "Send mail with title: $mailTitle\nBody:\n$mailBody\nTo: $recipient")

        return try {
            withContext(Dispatchers.IO) {
                mailSender.send(
                    message = message,
                    recipient = Recipient(value = recipient),
                    settings = settings
                )
            }
            Result.success()
        } catch (e: Throwable) {
            Log.e(LOG_TAG, "Email sending failed with exception ${e.javaClass} with message ${e.message}")
            if(e.message?.lowercase()?.contains("invalid addresses") == true) {
                notificationSender.showNotification(applicationContext.getString(R.string.error_incorrect_address, recipient))
            } else if (e.message?.lowercase()?.contains("authentication failed") == true) {
                notificationSender.showNotification(applicationContext.getString(R.string.error_authentication))
            } else if(e.message?.lowercase()?.contains("connect to host") == true) {
                notificationSender.showNotification(applicationContext.getString(R.string.error_connecting))
            } else {
                notificationSender.showNotification(applicationContext.getString(R.string.error_unexpected, recipient))
            }

            Result.failure()
        }
    }
}