package com.example.smstomail.di.modules

import com.example.smstomail.domain.service.IMessageSender
import com.example.smstomail.domain.service.MailSender
import dagger.Binds
import dagger.Module

@Module
abstract class MailSenderModule {
    @Binds
    abstract fun mailSender(sender: MailSender): IMessageSender
}