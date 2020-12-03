package com.example.smstomail

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.provider.Telephony.Sms.Intents.getMessagesFromIntent
import android.util.Log

class SMSReceiver: BroadcastReceiver() {
    companion object {
        const val LOG_TAG = "SMSReceiver"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            var isReceiverEnabled = false
            with(context.getSharedPreferences(PreferencesKeys.PREFERENCES_KEY, Context.MODE_PRIVATE)) {
                try {
                    isReceiverEnabled = getBoolean(PreferencesKeys.RECEIVER_ENABLED_KEY, false)
                } catch (ex: Resources.NotFoundException) {}
            }

            if (!isReceiverEnabled)
                return

            val smsSet = getMessagesFromIntent(intent).toSet()

            smsSet.forEach { smsObj ->
                Log.d(LOG_TAG, "Received sms from: ${smsObj.displayOriginatingAddress}\nwith body:\n${smsObj.displayMessageBody}")

                MailSendWork.scheduleWork(context, mailTitle = smsObj.displayOriginatingAddress, mailBody = smsObj.displayMessageBody)
            }
        }
    }
}