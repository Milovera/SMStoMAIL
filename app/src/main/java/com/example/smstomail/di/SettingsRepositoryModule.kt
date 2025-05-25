package com.example.smstomail.di

import com.example.smstomail.data.repository.ISettingsRepository
import com.example.smstomail.data.repository.SettingsRepository
import dagger.Binds
import dagger.Module

@Module
abstract class SettingsRepositoryModule {
    @Binds
    abstract fun settings(repository: SettingsRepository): ISettingsRepository
}