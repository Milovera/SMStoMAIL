package com.example.smstomail.di.components

import android.content.Context
import com.example.smstomail.di.modules.DatabaseModule
import com.example.smstomail.di.modules.PreferencesModule
import com.example.smstomail.di.modules.RepositoriesModule
import com.example.smstomail.domain.broadcastreceivers.SMSReceiver
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DatabaseModule::class,
        PreferencesModule::class,
        RepositoriesModule::class
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