package com.example.smstomail.di.modules

import com.example.smstomail.data.repository.FiltersRepository
import com.example.smstomail.data.repository.IFiltersRepository
import com.example.smstomail.data.repository.IRecipientsRepository
import com.example.smstomail.data.repository.ISettingsRepository
import com.example.smstomail.data.repository.RecipientsRepository
import com.example.smstomail.data.repository.SettingsRepository
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoriesModule {
    @Binds
    abstract fun filters(repository: FiltersRepository): IFiltersRepository
    @Binds
    abstract fun settings(repository: SettingsRepository): ISettingsRepository
    @Binds
    abstract fun recipients(repository: RecipientsRepository): IRecipientsRepository
}