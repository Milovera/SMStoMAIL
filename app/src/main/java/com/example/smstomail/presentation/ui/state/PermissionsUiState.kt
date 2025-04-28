package com.example.smstomail.presentation.ui.state

data class PermissionsUiState(
    val isSMSReceiveAllowed: Boolean = false,
    val isNotificationAllowed: Boolean = false
)
