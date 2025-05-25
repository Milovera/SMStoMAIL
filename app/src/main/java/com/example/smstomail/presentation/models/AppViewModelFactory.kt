package com.example.smstomail.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import kotlin.collections.iterator

class AppViewModelFactory @Inject constructor(
    private val viewModelsCreators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator = viewModelsCreators[modelClass]

        if(creator == null) {
            for((key, value) in viewModelsCreators) {
                if(modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }

        if(creator == null) {
            throw IllegalArgumentException("Unknown model class: $modelClass")
        }

        return try {
            @Suppress("UNCHECKED_CAST")
            creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}