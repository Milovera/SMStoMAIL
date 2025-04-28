package com.example.smstomail.domain.service

import com.example.smstomail.data.entity.Message
import com.example.smstomail.data.entity.Recipient
import com.example.smstomail.data.entity.SettingsData
import kotlin.jvm.Throws

interface IMessageSender {
    @Throws(Throwable::class)
    fun send(message: Message, recipient: Recipient, settings: SettingsData)
}