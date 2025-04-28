package com.example.smstomail.presentation.ui.state

import com.example.smstomail.data.entity.Recipient

data class SettingsUiStateData(
    val login: String = "",
    val password: String = "",
    val host: String = "",
    val sslPort: String = "",
    val isReceiverEnabled: Boolean = false,
    val recipientsList: List<Recipient> = emptyList()
)