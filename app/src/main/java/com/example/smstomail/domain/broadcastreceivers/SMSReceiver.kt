package com.example.smstomail.domain.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms.Intents.getMessagesFromIntent
import android.util.Log
import androidx.work.WorkManager
import com.example.smstomail.data.database.dao.AppDatabase
import com.example.smstomail.data.datasource.EncryptedPreferencesStringDataSource
import com.example.smstomail.domain.workers.MailSenderWorker
import com.example.smstomail.data.entity.Message
import com.example.smstomail.data.repository.FiltersRepository
import com.example.smstomail.data.repository.RecipientsRepository
import com.example.smstomail.data.repository.SettingsRepository
import com.example.smstomail.domain.service.FilterStrategy
import com.example.smstomail.domain.service.toStrategy
import kotlinx.coroutines.runBlocking

class SMSReceiver: BroadcastReceiver() {
    companion object {
        const val LOG_TAG = "SMSReceiver"
    }

    private fun getFilters(context: Context): List<FilterStrategy> {
        return runBlocking {
            FiltersRepository(
                AppDatabase.getDatabase(context).filtersDao()
            )
                .getItems()
                .map {
                    it.toStrategy()
                }
        }
    }
    private fun getRecipients(context: Context): List<String> {
        return runBlocking {
            RecipientsRepository(
                AppDatabase.getDatabase(context).recipientsDao()
            )
                .getItems()
                .map {  recipient ->
                    recipient.value
                }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.v(LOG_TAG, "onReceive with action ${intent?.action}")

        if (context != null && intent != null) {
            val isReceiverEnabled = SettingsRepository(EncryptedPreferencesStringDataSource(context))
                .read()
                ?.isReceiverEnabled

            if (isReceiverEnabled != true)
                return

            val smsSet = getMessagesFromIntent(intent).toSet()
            val filters = getFilters(context)
            val recipients = getRecipients(context)

            val messagesList = smsSet
                .map { sms ->
                    Log.v(LOG_TAG, "Received sms from: ${sms.displayOriginatingAddress}\nwith body:\n${sms.displayMessageBody}")

                    Message(
                        sender = sms.displayOriginatingAddress,
                        body = sms.displayMessageBody
                    )
                }
                .filter {  message ->
                    filters.find { filter ->
                        !filter.pass(message)
                    } == null
                }

            Log.v(LOG_TAG, "Received ${smsSet.size} sms\nSending ${messagesList.size}")

            messagesList.forEach { message ->
                recipients.forEach { recipient ->
                    MailSenderWorker.createRequest(
                        message = message,
                        recipient = recipient
                    ).also { workRequest ->
                        WorkManager.getInstance(context).enqueue(workRequest)
                    }
                }
            }
        }
    }
}