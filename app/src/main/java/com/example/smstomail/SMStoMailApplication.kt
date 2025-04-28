package com.example.smstomail

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.smstomail.domain.di.AppContainer
import com.example.smstomail.domain.di.IAppContainer
import com.example.smstomail.domain.workers.AppWorkerFactory

class SMStoMailApplication: Application()  {
    lateinit var container: IAppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(context = this)

        val workerFactory = AppWorkerFactory(
            notificationSender = container.notificationSender,
            mailSender = container.mailSender,
            settingsRepository = container.settingsRepository
        )

        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

        WorkManager.initialize(this, workManagerConfig)
    }
}