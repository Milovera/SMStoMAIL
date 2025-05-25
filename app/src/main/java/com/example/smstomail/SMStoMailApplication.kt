package com.example.smstomail

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.smstomail.di.AppComponent
import com.example.smstomail.di.DaggerAppComponent
import com.example.smstomail.domain.workers.AppWorkerFactory

class SMStoMailApplication: Application()  {
    val appComponent: AppComponent by lazy {
        Log.v("init", "AppComponent")
        DaggerAppComponent.factory().create(
            context = applicationContext
        )
    }

    override fun onCreate() {
        super.onCreate()
        Log.v("init", "Application")

        val workerFactory = AppWorkerFactory(
            notificationSender = appComponent.notificationSender(),
            mailSender = appComponent.mailSender(),
            settingsRepository = appComponent.settings()
        )

        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

        WorkManager.initialize(this, workManagerConfig)
    }
}