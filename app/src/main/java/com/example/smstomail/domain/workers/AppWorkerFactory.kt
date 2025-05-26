package com.example.smstomail.domain.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.smstomail.data.repository.ISettingsRepository
import com.example.smstomail.domain.service.IMessageSender
import com.example.smstomail.domain.service.INotificationSender
import javax.inject.Inject

class AppWorkerFactory @Inject constructor(
    private val notificationSender: INotificationSender,
    private val mailSender: IMessageSender,
    private val settingsRepository: ISettingsRepository
): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val clazz = Class.forName(workerClassName)

        return when {
            clazz.isAssignableFrom(MailSenderWorker::class.java) -> {
                MailSenderWorker(appContext, workerParameters, notificationSender, mailSender, settingsRepository)
            }
            else -> null
        }
    }
}