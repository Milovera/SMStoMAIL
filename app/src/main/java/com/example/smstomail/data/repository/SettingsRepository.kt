package com.example.smstomail.data.repository

import com.example.smstomail.data.datasource.IStringDataSource
import com.example.smstomail.data.entity.SettingsData

class SettingsRepository(
    private val dataSource: IStringDataSource
): ISettingsRepository {
    override fun read(): SettingsData? {
        return try {
            SettingsData(
                login = dataSource.read(SettingsData.Keys.LOGIN.name) ?: "",
                password = dataSource.read(SettingsData.Keys.PASSWORD.name) ?: "",
                host = dataSource.read(SettingsData.Keys.SMTP_SERVER_HOST.name) ?: "",
                sslPort = dataSource.read(SettingsData.Keys.SSL_PORT.name)?.toIntOrNull(),
                isReceiverEnabled = dataSource.read(SettingsData.Keys.IS_RECEIVER_ENABLED.name)?.toBoolean() == true
            )
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    override fun write(data: SettingsData) {
        dataSource.write(SettingsData.Keys.LOGIN.name, data.login)
        dataSource.write(SettingsData.Keys.PASSWORD.name, data.password)
        dataSource.write(SettingsData.Keys.SSL_PORT.name, data.sslPort.toString())
        dataSource.write(SettingsData.Keys.SMTP_SERVER_HOST.name, data.host)
        dataSource.write(SettingsData.Keys.IS_RECEIVER_ENABLED.name, data.isReceiverEnabled.toString())
    }
}