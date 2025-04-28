package com.example.smstomail.presentation.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smstomail.data.entity.Filter
import com.example.smstomail.data.entity.ItemSnapshot
import com.example.smstomail.domain.interactors.FiltersInteractor
import com.example.smstomail.presentation.ui.state.FiltersUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FiltersViewModel(
    private val filtersInteractor: FiltersInteractor
) : ViewModel() {
    companion object {
        const val LOG_TAG = "FiltersViewModel"
    }

    val filtersUiState: StateFlow<FiltersUiState> = filtersInteractor.itemsStateFlow
        .filterNotNull()
        .map { snapshot: ItemSnapshot<List<Filter>>  ->
            FiltersUiState(
                item = snapshot.item,
                isValid = snapshot.isValid,
                isModified = snapshot.isModified
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = FiltersUiState(
                item = emptyList(),
                isModified = false,
                isValid = true
            )
        )

    init {
        reset()
    }

    fun reset() {
        Log.i(LOG_TAG, "reset()")
        viewModelScope.launch(Dispatchers.IO) {
            filtersInteractor.reset()
        }
    }

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            filtersInteractor.save()
        }
    }

    fun createNewFilter() {
        filtersInteractor.createNewItem()
    }

    fun updateFilter(filterId: Int, filterType: Filter.Type, value: String) {
        filtersInteractor.updateItem(filterId, Filter(filterId, filterType, value))
    }

    fun deleteFilter(filterId: Int) {
        filtersInteractor.deleteItem(filterId)
    }
}