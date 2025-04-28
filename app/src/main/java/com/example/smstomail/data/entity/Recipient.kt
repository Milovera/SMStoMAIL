package com.example.smstomail.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipients")
data class Recipient(
    @PrimaryKey
    val id: Int = 0,
    val value: String = ""
) {
    companion object {
        val emailRegex = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")
    }

    val isValid: Boolean
        get() = value.matches(emailRegex)
}
