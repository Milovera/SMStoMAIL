package com.example.smstomail.domain.interactors

import com.example.smstomail.data.entity.ItemSnapshot
import com.example.smstomail.data.entity.SettingsData
import kotlinx.coroutines.flow.StateFlow

interface ISettingsInteractor {
    val settingsStateFlow: StateFlow<ItemSnapshot<SettingsData>>

    suspend fun reset()
    suspend fun save()
    fun updateLogin(value: String)
    fun updatePassword(value: String)
    fun updateHost(value: String)
    fun updatePort(value: String)
    suspend fun toggleReceiverStatus()
}