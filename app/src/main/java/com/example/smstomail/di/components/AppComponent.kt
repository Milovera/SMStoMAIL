package com.example.smstomail.di.components

import android.content.Context
import com.example.smstomail.MainActivity
import com.example.smstomail.di.modules.DatabaseModule
import com.example.smstomail.di.modules.InteractorsModule
import com.example.smstomail.di.modules.MailSenderModule
import com.example.smstomail.di.modules.NotificationModule
import com.example.smstomail.di.modules.PreferencesModule
import com.example.smstomail.di.modules.RepositoriesModule
import com.example.smstomail.di.modules.ViewModelsModule
import com.example.smstomail.domain.workers.AppWorkerFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DatabaseModule::class,
        PreferencesModule::class,
        RepositoriesModule::class,
        InteractorsModule::class,
        ViewModelsModule::class,
        MailSenderModule::class,
        NotificationModule::class,
    ]
)
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: MainActivity)

    fun workerFactory(): AppWorkerFactory
}