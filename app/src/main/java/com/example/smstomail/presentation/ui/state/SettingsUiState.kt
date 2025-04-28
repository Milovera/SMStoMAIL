package com.example.smstomail.presentation.ui.state

data class SettingsUiState(
    override val item: SettingsUiStateData = SettingsUiStateData(),
    override val isValid: Boolean = false,
    override val isModified: Boolean = false
): IItemUiState<SettingsUiStateData>