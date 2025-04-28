package com.example.smstomail.presentation.ui.state

import com.example.smstomail.data.entity.Filter

data class FiltersUiState(
    override val item: List<Filter>,
    override val isValid: Boolean,
    override val isModified: Boolean
): IItemUiState<List<Filter>>