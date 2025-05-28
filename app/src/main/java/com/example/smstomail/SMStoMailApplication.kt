package com.example.smstomail

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.smstomail.di.components.AppComponent
import com.example.smstomail.di.components.DaggerAppComponent

class SMStoMailApplication: Application()  {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(
            context = applicationContext
        )
    }

    override fun onCreate() {
        super.onCreate()

        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(
                appComponent.workerFactory()
            )
            .build()

        WorkManager.initialize(this, workManagerConfig)
    }
}