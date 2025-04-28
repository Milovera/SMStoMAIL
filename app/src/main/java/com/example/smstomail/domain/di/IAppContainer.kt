package com.example.smstomail.domain.di

import com.example.smstomail.data.database.dao.IFilterDao
import com.example.smstomail.data.database.dao.IRecipientDao
import com.example.smstomail.data.repository.IFiltersRepository
import com.example.smstomail.data.repository.IRecipientsRepository
import com.example.smstomail.data.repository.ISettingsRepository
import com.example.smstomail.domain.interactors.FiltersInteractor
import com.example.smstomail.domain.interactors.RecipientsInteractor
import com.example.smstomail.domain.interactors.SettingsInteractor
import com.example.smstomail.domain.service.IMessageSender
import com.example.smstomail.domain.service.INotificationSender

interface IAppContainer {
    val settingsRepository: ISettingsRepository
    val recipientsRepository: IRecipientsRepository
    val filtersRepository: IFiltersRepository
    val recipientsInteractor: RecipientsInteractor
    val filtersInteractor: FiltersInteractor
    val settingsInteractor: SettingsInteractor
    val recipientsDao: IRecipientDao
    val filtersDao: IFilterDao
    val mailSender: IMessageSender
    val notificationSender: INotificationSender
}