package com.example.smstomail.di

import com.example.smstomail.data.repository.FiltersRepository
import com.example.smstomail.data.repository.IFiltersRepository
import dagger.Binds
import dagger.Module

@Module
abstract class FiltersRepositoryModule {
    @Binds
    abstract fun filters(repository: FiltersRepository): IFiltersRepository
}