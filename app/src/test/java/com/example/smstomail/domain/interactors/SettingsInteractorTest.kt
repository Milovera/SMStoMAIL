package com.example.smstomail.domain.interactors

import com.example.smstomail.data.entity.SettingsData
import com.example.smstomail.data.repository.ISettingsRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SettingsInteractorTest {
    private val settingsRepositoryMock = object: ISettingsRepository {
        var savedData = SettingsData(
            login = "login",
            password = "password",
            host = "host",
            sslPort = 445,
            isReceiverEnabled = false
        )

        override fun read() = savedData
        override fun write(data: SettingsData) { savedData = data }
    }
    private val settingsInteractor = SettingsInteractor(settingsRepositoryMock)

    @BeforeEach
    fun setup() = runTest {
        settingsInteractor.reset()
    }

    @Test
    fun updateLogin_repositoryNotModified() {
        // when
        settingsInteractor.updateLogin("newLogin")

        // then
        assert(settingsRepositoryMock.savedData.login == "login")
        assert(settingsInteractor.settingsStateFlow.value.isModified == true)
        assert(settingsInteractor.settingsStateFlow.value.isValid == true)
    }

    @Test
    fun updatePassword_repositoryNotModified() {
        // when
        settingsInteractor.updatePassword("newPassword")

        // then
        assert(settingsRepositoryMock.savedData.password == "password")
        assert(settingsInteractor.settingsStateFlow.value.isModified == true)
        assert(settingsInteractor.settingsStateFlow.value.isValid == true)
    }

    @Test
    fun updateHost_repositoryNotModified() {
        // when
        settingsInteractor.updateHost("newHost")

        // then
        assert(settingsRepositoryMock.savedData.host == "host")
        assert(settingsInteractor.settingsStateFlow.value.isModified == true)
        assert(settingsInteractor.settingsStateFlow.value.isValid == true)
    }

    @Test
    fun updatePort_repositoryNotModified() {
        // when
        settingsInteractor.updatePort("440")

        // then
        assert(settingsRepositoryMock.savedData.sslPort == 445)
        assert(settingsInteractor.settingsStateFlow.value.isModified == true)
        assert(settingsInteractor.settingsStateFlow.value.isValid == true)
    }

    @Test
    fun toggleReceiverStatus_repositoryModified() = runTest {
        // when
        settingsInteractor.toggleReceiverStatus()

        // then
        assert(settingsRepositoryMock.savedData.isReceiverEnabled == true)
        assert(settingsInteractor.settingsStateFlow.value.isModified == false)
        assert(settingsInteractor.settingsStateFlow.value.isValid == true)
    }

    @Test
    fun updateSettingsAndSave_repositoryModified() = runTest {
        // when
        settingsInteractor.updateLogin("newLogin")
        settingsInteractor.updatePassword("newPassword")
        settingsInteractor.updatePort("443")
        settingsInteractor.updateHost("newHost")
        settingsInteractor.save()

        // then
        assert(settingsInteractor.settingsStateFlow.value.isModified == false)
        assert(settingsInteractor.settingsStateFlow.value.isValid == true)
        val savedSettings = settingsRepositoryMock.savedData
        assert(savedSettings.login == "newLogin")
        assert(savedSettings.password == "newPassword")
        assert(savedSettings.sslPort == 443)
        assert(savedSettings.host == "newHost")
    }

    @Test
    fun updateInvalidLogin_invalidFlag() {
        // when
        settingsInteractor.updateLogin("")

        // then
        assert(settingsInteractor.settingsStateFlow.value.isModified == true)
        assert(settingsInteractor.settingsStateFlow.value.isValid == false)
    }

    @Test
    fun updateInvalidPassword_invalidFlag() {
        // when
        settingsInteractor.updatePassword("")

        // then
        assert(settingsInteractor.settingsStateFlow.value.isModified == true)
        assert(settingsInteractor.settingsStateFlow.value.isValid == false)
    }

    @Test
    fun updateInvalidHost_invalidFlag() {
        // when
        settingsInteractor.updateHost("")

        // then
        assert(settingsInteractor.settingsStateFlow.value.isModified == true)
        assert(settingsInteractor.settingsStateFlow.value.isValid == false)
    }

    @Test
    fun updateInvalidPort_invalidFlag() {
        // when
        settingsInteractor.updatePort("")

        // then
        assert(settingsInteractor.settingsStateFlow.value.isModified == true)
        assert(settingsInteractor.settingsStateFlow.value.isValid == false)
    }
}