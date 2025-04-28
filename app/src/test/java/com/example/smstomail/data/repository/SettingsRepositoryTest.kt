package com.example.smstomail.data.repository
import com.example.smstomail.data.datasource.IStringDataSource
import com.example.smstomail.data.entity.SettingsData
import org.junit.jupiter.api.Test

class SettingsRepositoryTest {
    private val settings = SettingsData(
        login = "login",
        password = "password",
        host = "host.host.host",
        sslPort = 432,
        isReceiverEnabled = true
    )

    private val dataSource = object: IStringDataSource {
        val dataMap: MutableMap<String, String> = mutableMapOf()
        override fun read(key: String): String? {
            return dataMap[key]
        }

        override fun write(key: String, value: String) {
            dataMap[key] = value
        }
    }
    private val repository = SettingsRepository(dataSource = dataSource)

    @Test
    fun repositorySave_insertAllKeys() {
        repository.write(settings)

        SettingsData.Keys.entries.forEach {  key ->
            assert(dataSource.dataMap.contains(key.name))
        }
    }

    @Test
    fun repositoryRead_returnSettingsData() {
        repository.write(settings)
        val savedSettings = repository.read()

        assert(savedSettings == settings)
    }

    @Test
    fun repositoryReadInvalidSettings_returnNull() {
        val key = SettingsData.Keys.entries.first().name
        dataSource.write(key, "someValue")

        assert(repository.read() == null)
    }
}