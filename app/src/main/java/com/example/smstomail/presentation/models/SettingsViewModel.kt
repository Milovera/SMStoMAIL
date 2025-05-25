package com.example.smstomail.presentation.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smstomail.data.entity.ItemSnapshot
import com.example.smstomail.data.entity.Recipient
import com.example.smstomail.domain.interactors.AbstractItemListBufferInteractor
import com.example.smstomail.domain.interactors.ISettingsInteractor
import com.example.smstomail.presentation.ui.state.SettingsUiState
import com.example.smstomail.presentation.ui.state.SettingsUiStateData
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Singleton
import kotlin.collections.List

@Singleton
class SettingsViewModel @Inject constructor(
    private val settingsInteractor: ISettingsInteractor,
    private val recipientsInteractor: AbstractItemListBufferInteractor<Recipient>
) : ViewModel() {
    companion object {
        const val LOG_TAG = "SettingsViewModel"
    }

    val settingsUiState: StateFlow<SettingsUiState> = combine (
        settingsInteractor.settingsStateFlow,
        recipientsInteractor.itemsStateFlow
    ) { settingsSnapshot, recipientsSnapshot: ItemSnapshot<List<Recipient>> ->
        SettingsUiState(
            item = SettingsUiStateData(
                login = settingsSnapshot.item.login,
                password = settingsSnapshot.item.password,
                host = settingsSnapshot.item.host,
                sslPort = settingsSnapshot.item.sslPort?.toString() ?: "",
                isReceiverEnabled = settingsSnapshot.item.isReceiverEnabled,
                recipientsList = recipientsSnapshot.item
            ),
            isModified = settingsSnapshot.isModified || recipientsSnapshot.isModified,
            isValid = settingsSnapshot.isValid && recipientsSnapshot.isValid
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = SettingsUiState()
    )

    init {
        reset()
        Log.v("init", "SettingsViewModel")
    }

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block()
        }
    }

    fun save() {
        Log.i(LOG_TAG, "save()")
        launch {
            recipientsInteractor.save()
            settingsInteractor.save()
        }
    }
    fun reset() {
        Log.i(LOG_TAG, "reset()")
        launch {
            recipientsInteractor.reset()
            settingsInteractor.reset()
        }
    }
    fun updateLogin(value: String) {
        Log.i(LOG_TAG, "updateLogin()")
        settingsInteractor.updateLogin(value)
    }
    fun updatePassword(value: String) {
        Log.i(LOG_TAG, "updatePassword()")
        settingsInteractor.updatePassword(value)
    }
    fun updateHost(value: String) {
        Log.i(LOG_TAG, "updateHost()")
        settingsInteractor.updateHost(value)
    }
    fun updatePort(value: String) {
        Log.i(LOG_TAG, "updatePort()")
        settingsInteractor.updatePort(value)
    }
    fun toggleReceiverStatus() {
        Log.i(LOG_TAG, "updateReceiverStatus()")
        launch {
            settingsInteractor.toggleReceiverStatus()
        }
    }
    fun createNewRecipient() {
        Log.i(LOG_TAG, "createNewRecipient()")
        recipientsInteractor.createNewItem()
    }
    fun updateRecipient(recipientId: Int, recipientValue: String) {
        Log.i(LOG_TAG, "createNewRecipient()")
        recipientsInteractor.updateItem(recipientId, Recipient(recipientId, recipientValue))
    }
    fun deleteRecipient(recipientId: Int) {
        recipientsInteractor.deleteItem(recipientId)
    }
}
