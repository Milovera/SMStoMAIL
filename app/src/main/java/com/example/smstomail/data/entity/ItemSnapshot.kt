package com.example.smstomail.data.entity

data class ItemSnapshot<T>(
    val item: T,
    val isModified: Boolean,
    val isValid: Boolean
)