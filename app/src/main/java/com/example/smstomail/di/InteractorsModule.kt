package com.example.smstomail.di

import com.example.smstomail.data.entity.Filter
import com.example.smstomail.data.entity.Recipient
import com.example.smstomail.domain.interactors.AbstractItemListBufferInteractor
import com.example.smstomail.domain.interactors.FiltersInteractor
import com.example.smstomail.domain.interactors.ISettingsInteractor
import com.example.smstomail.domain.interactors.RecipientsInteractor
import com.example.smstomail.domain.interactors.SettingsInteractor
import dagger.Binds
import dagger.Module

@Module
abstract class InteractorsModule {
    @Binds
    abstract fun recipients(interactor: RecipientsInteractor): AbstractItemListBufferInteractor<Recipient>
    @Binds
    abstract fun filters(interactor: FiltersInteractor): AbstractItemListBufferInteractor<Filter>
    @Binds
    abstract fun settings(interactor: SettingsInteractor): ISettingsInteractor
}