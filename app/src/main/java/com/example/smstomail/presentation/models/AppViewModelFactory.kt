package com.example.smstomail.presentation.models

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.smstomail.SMStoMailApplication

object AppViewModelFactory {
    val Factory = viewModelFactory {
        initializer {
            val container = myApplication().container
            SettingsViewModel(
                settingsInteractor = container.settingsInteractor,
                recipientsInteractor = container.recipientsInteractor
            )
        }
        initializer {
            PermissionsViewModel(
                application = myApplication()
            )
        }
        initializer {
            FiltersViewModel(
                filtersInteractor = myApplication().container.filtersInteractor
            )
        }
    }
}

fun CreationExtras.myApplication(): SMStoMailApplication = (this[APPLICATION_KEY] as SMStoMailApplication)