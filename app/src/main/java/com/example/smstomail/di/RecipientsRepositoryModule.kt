package com.example.smstomail.di

import com.example.smstomail.data.repository.IRecipientsRepository
import com.example.smstomail.data.repository.RecipientsRepository
import dagger.Binds
import dagger.Module

@Module
abstract class RecipientsRepositoryModule {
    @Binds
    abstract fun recipients(repository: RecipientsRepository): IRecipientsRepository
}