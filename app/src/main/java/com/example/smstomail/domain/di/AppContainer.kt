package com.example.smstomail.domain.di

import android.content.Context
import android.os.Build
import com.example.smstomail.data.database.dao.AppDatabase
import com.example.smstomail.data.database.dao.IFilterDao
import com.example.smstomail.data.database.dao.IRecipientDao
import com.example.smstomail.data.datasource.EncryptedPreferencesStringDataSource
import com.example.smstomail.data.repository.FiltersRepository
import com.example.smstomail.data.repository.IFiltersRepository
import com.example.smstomail.data.repository.IRecipientsRepository
import com.example.smstomail.data.repository.ISettingsRepository
import com.example.smstomail.data.repository.RecipientsRepository
import com.example.smstomail.data.repository.SettingsRepository
import com.example.smstomail.domain.interactors.FiltersInteractor
import com.example.smstomail.domain.interactors.RecipientsInteractor
import com.example.smstomail.domain.service.INotificationSender
import com.example.smstomail.domain.service.LegacyNotificationSender
import com.example.smstomail.domain.service.MailSender
import com.example.smstomail.domain.service.NotificationSender
import com.example.smstomail.R
import com.example.smstomail.domain.interactors.SettingsInteractor

class AppContainer(
    private val context: Context
): IAppContainer {
    override val settingsRepository: ISettingsRepository by lazy {
        SettingsRepository(
            EncryptedPreferencesStringDataSource(context)
        )
    }
    override val recipientsRepository: IRecipientsRepository by lazy {
        RecipientsRepository(recipientsDao)
    }
    override val filtersRepository: IFiltersRepository by lazy {
        FiltersRepository(filtersDao)
    }
    override val recipientsDao: IRecipientDao by lazy {
        AppDatabase.getDatabase(context).recipientsDao()
    }
    override val filtersDao: IFilterDao by lazy {
        AppDatabase.getDatabase(context).filtersDao()
    }
    override val recipientsInteractor by lazy {
        RecipientsInteractor(recipientsRepository)
    }
    override val filtersInteractor: FiltersInteractor by lazy {
        FiltersInteractor(filtersRepository)
    }
    override val settingsInteractor: SettingsInteractor by lazy {
        SettingsInteractor(settingsRepository)
    }
    override val mailSender= MailSender()
    override val notificationSender: INotificationSender by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationSender.createNotificationChannel(
                context = context,
                channelName = context.getString(R.string.notification_channel_name),
                channelDescription = context.getString(R.string.notification_channel_description)
            )
            NotificationSender(context)
        } else {
            LegacyNotificationSender(context)
        }
    }
}