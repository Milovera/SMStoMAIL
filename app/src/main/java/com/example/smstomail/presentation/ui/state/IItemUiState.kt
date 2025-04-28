package com.example.smstomail.presentation.ui.state

interface IItemUiState<T> {
    val item: T
    val isValid: Boolean
    val isModified: Boolean
}