package com.example.smstomail.di

import android.content.Context
import com.example.smstomail.domain.broadcastreceivers.SMSReceiver
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DatabaseModule::class,
        PreferencesModule::class,
        FiltersRepositoryModule::class,
        SettingsRepositoryModule::class,
        RecipientsRepositoryModule::class
    ]
)
interface ReceiverComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): ReceiverComponent
    }

    fun inject(receiver: SMSReceiver)
}