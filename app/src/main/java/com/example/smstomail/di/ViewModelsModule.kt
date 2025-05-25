package com.example.smstomail.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smstomail.presentation.models.AppViewModelFactory
import com.example.smstomail.presentation.models.FiltersViewModel
import com.example.smstomail.presentation.models.PermissionsViewModel
import com.example.smstomail.presentation.models.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelsModule {
    @Binds
    abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ClassKey(FiltersViewModel::class)
    abstract fun bindFilters(model: FiltersViewModel): ViewModel

    @Binds
    @IntoMap
    @ClassKey(PermissionsViewModel::class)
    abstract fun bindPermissions(model: PermissionsViewModel): ViewModel

    @Binds
    @IntoMap
    @ClassKey(SettingsViewModel::class)
    abstract fun bindSettings(model: SettingsViewModel): ViewModel
}