package com.example.smstomail.di

import com.example.smstomail.data.datasource.EncryptedPreferencesStringDataSource
import com.example.smstomail.data.datasource.IStringDataSource
import dagger.Binds
import dagger.Module

@Module
abstract class PreferencesModule {
    @Binds
    abstract fun settingsDataSource(dataSource: EncryptedPreferencesStringDataSource): IStringDataSource
}