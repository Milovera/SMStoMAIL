package com.example.smstomail.domain.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms.Intents.getMessagesFromIntent
import android.util.Log
import androidx.work.WorkManager
import com.example.smstomail.data.entity.Message
import com.example.smstomail.data.repository.IFiltersRepository
import com.example.smstomail.data.repository.IRecipientsRepository
import com.example.smstomail.data.repository.ISettingsRepository
import com.example.smstomail.di.components.DaggerReceiverComponent
import com.example.smstomail.domain.service.FilterStrategy
import com.example.smstomail.domain.service.toStrategy
import com.example.smstomail.domain.workers.MailSenderWorker
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SMSReceiver: BroadcastReceiver() {
    companion object {
        const val LOG_TAG = "SMSReceiver"
    }

    @Inject
    lateinit var recipientsRepository: IRecipientsRepository
    @Inject
    lateinit var filtersRepository: IFiltersRepository
    @Inject
    lateinit var settingsRepository: ISettingsRepository

    private fun getFilters(): List<FilterStrategy> {
        return runBlocking {
            filtersRepository
                .getItems()
                .map {
                    it.toStrategy()
                }
        }
    }
    private fun getRecipients(): List<String> {
        return runBlocking {
            recipientsRepository
                .getItems()
                .map {  recipient ->
                    recipient.value
                }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.v(LOG_TAG, "onReceive with action ${intent?.action}")

        if (context != null && intent != null) {
            DaggerReceiverComponent.factory().create(context).inject(this)

            val isReceiverEnabled = settingsRepository.read()?.isReceiverEnabled

            if (isReceiverEnabled != true)
                return

            val smsSet = getMessagesFromIntent(intent).toSet()
            val filters = getFilters()
            val recipients = getRecipients()

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