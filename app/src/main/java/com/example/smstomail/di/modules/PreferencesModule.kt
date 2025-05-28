package com.example.smstomail.di.modules

import com.example.smstomail.data.datasource.EncryptedPreferences
import com.example.smstomail.data.datasource.IStringDataSource
import dagger.Binds
import dagger.Module

@Module
abstract class PreferencesModule {
    @Binds
    abstract fun settingsDataSource(dataSource: EncryptedPreferences): IStringDataSource
}