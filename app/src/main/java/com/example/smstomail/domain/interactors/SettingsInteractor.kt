package com.example.smstomail.domain.interactors

import com.example.smstomail.data.entity.ItemSnapshot
import com.example.smstomail.data.entity.SettingsData
import com.example.smstomail.data.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsInteractor(
    private val settingsRepository: ISettingsRepository
) {
    private var savedSettings: SettingsData? = null
    var currentSettings: SettingsData? = null
        private set
    private val settingsMutableStateFlow = MutableStateFlow<ItemSnapshot<SettingsData>>(
        ItemSnapshot<SettingsData>(
            item = SettingsData(),
            isModified = false,
            isValid = false
        )
    )
    val settingsStateFlow = settingsMutableStateFlow.asStateFlow()

    private fun isValidSettings(settings: SettingsData): Boolean {
        return with(settings) {
                login.isNotEmpty() && password.isNotEmpty() &&
                        host.isNotEmpty() && sslPort != null && sslPort > 0 && sslPort < 65536
            }
    }
    private fun emitUpdates() {
        currentSettings?.let { settings ->
            settingsMutableStateFlow.update {
                ItemSnapshot(
                    item = settings,
                    isModified = settings.hashCode() != (savedSettings?.hashCode() ?: -1),
                    isValid = isValidSettings(settings)
                )
            }
        }
    }
    suspend fun reset() {
        currentSettings = savedSettings ?: settingsRepository.read()
            .also {settingsFromRepository ->
                savedSettings = settingsFromRepository
            }
        emitUpdates()
    }
    suspend fun save() {
        currentSettings?.let { settings ->
            if(isValidSettings(settings)) {
                settingsRepository.write(settings)
                savedSettings = settings
                emitUpdates()
            }
        }
    }
    fun updateLogin(value: String) {
        currentSettings = currentSettings?.copy(
            login = value
        )
        emitUpdates()
    }
    fun updatePassword(value: String) {
        currentSettings = currentSettings?.copy(
            password = value
        )
        emitUpdates()
    }
    fun updateHost(value: String) {
        currentSettings = currentSettings?.copy(
            host = value
        )
        emitUpdates()
    }
    fun updatePort(value: String) {
        var newPort = value.toIntOrNull()

        if(newPort != null && (newPort < 1 || newPort > 65535)) {
            newPort = null
        }

        currentSettings = currentSettings?.copy(
            sslPort = newPort
        )
        emitUpdates()
    }
    suspend fun toggleReceiverStatus() {
        val newReceiverStatus = currentSettings?.isReceiverEnabled == false
        currentSettings = currentSettings?.copy(
            isReceiverEnabled = newReceiverStatus
        )
        save()
        emitUpdates()
    }
}