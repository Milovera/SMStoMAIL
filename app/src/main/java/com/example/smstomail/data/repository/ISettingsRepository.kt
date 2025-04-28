package com.example.smstomail.data.repository

import com.example.smstomail.data.entity.SettingsData

interface ISettingsRepository {
    fun read(): SettingsData?
    fun write(data: SettingsData)
}