package com.example.smstomail.di

import android.content.Context
import com.example.smstomail.MainActivity
import com.example.smstomail.data.repository.ISettingsRepository
import com.example.smstomail.domain.service.IMessageSender
import com.example.smstomail.domain.service.INotificationSender
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DatabaseModule::class,
        NotificationModule::class,
        MailSenderModule::class,
        FiltersRepositoryModule::class,
        SettingsRepositoryModule::class,
        RecipientsRepositoryModule::class,
        InteractorsModule::class,
        PreferencesModule::class,
        ViewModelsModule::class
    ]
)
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): AppComponent
    }

    fun inject(activity: MainActivity)

    fun notificationSender(): INotificationSender
    fun mailSender(): IMessageSender
    fun settings(): ISettingsRepository
}

