package com.example.smstomail.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filters")
data class Filter(
    @PrimaryKey
    val id: Int = 0,
    val type: Type = Type.SenderInclude,
    val value: String = ""
) {
    enum class Type {
        SenderInclude,
        SenderExclude,
        MessageInclude,
        MessageExclude
    }
}