package com.example.smstomail.presentations.models

import com.example.smstomail.data.entity.ItemSnapshot
import com.example.smstomail.data.entity.Recipient
import com.example.smstomail.data.entity.SettingsData
import com.example.smstomail.data.repository.mockAndroidLog
import com.example.smstomail.data.repository.unmockAndroidLog
import com.example.smstomail.domain.interactors.RecipientsInteractor
import com.example.smstomail.domain.interactors.SettingsInteractor
import com.example.smstomail.presentation.models.SettingsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SettingsViewModelTest {
    private val recipientsFlow = MutableStateFlow<ItemSnapshot<List<Recipient>>>(
        ItemSnapshot<List<Recipient>>(
            item = emptyList(),
            isValid = false,
            isModified = false
        )
    )
    private val recipientsInteractor = mockk<RecipientsInteractor> {
        coEvery { reset() } returns Unit
        coEvery { save() } returns Unit
        every { createNewItem() } returns Unit
        every { deleteItem(any()) } returns Unit
        every { itemsStateFlow } returns this@SettingsViewModelTest.recipientsFlow.asStateFlow()
        every { updateItem(any(), any()) } returns Unit
    }
    private val settingsFlow = MutableStateFlow<ItemSnapshot<SettingsData>>(
        ItemSnapshot<SettingsData>(
            item = SettingsData(
                login = "login",
                password = "password",
                host = "host",
                sslPort = 443,
                isReceiverEnabled = false
            ),
            isValid = true,
            isModified = false
        )
    )
    private val settingsInteractor = mockk<SettingsInteractor> {
        coEvery { reset() } returns Unit
        coEvery { save() } returns Unit
        every { updateLogin(any()) } returns Unit
        every { updatePassword(any()) } returns Unit
        every { updateHost(any()) } returns Unit
        every { updatePort(any()) } returns Unit
        coEvery { toggleReceiverStatus() } returns Unit
        every { settingsStateFlow } returns settingsFlow.asStateFlow()
    }
    private val viewModel by lazy {
        SettingsViewModel(settingsInteractor, recipientsInteractor)
    }
    private lateinit var testDispatcher: TestDispatcher

    fun updateSettings() {
        viewModel.settingsUiState.value
        viewModel.updateHost("newHost")
        viewModel.updatePort("444")
        viewModel.updateLogin("newLogin")
        viewModel.updatePassword("newPassword")
    }

    fun verifyNotSaved() {
        coVerify(exactly = 0) { recipientsInteractor.save() }
        coVerify(exactly = 0) { settingsInteractor.save() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setUp() {
        mockAndroidLog()
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        unmockAndroidLog()
        Dispatchers.resetMain()
    }

    @Test
    fun reset_callResetOnRecipientsAndSettingsInteractor() {
        // given
        val beforeUpdate = viewModel.settingsUiState.value
        updateSettings()

        // when
        viewModel.reset()
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        coVerify(exactly = 2) { recipientsInteractor.reset() } // first call while viewModel init
        coVerify(exactly = 2) { settingsInteractor.reset() }
        coVerify(exactly = 0) { recipientsInteractor.save() }
        coVerify(exactly = 0) { settingsInteractor.save() }

        assertEquals(beforeUpdate, viewModel.settingsUiState.value)
    }

    @Test
    fun deleteRecipient_callDeleteOnInteractor() {
        // when
        viewModel.deleteRecipient(123)

        // then
        verify(exactly = 1) { recipientsInteractor.deleteItem(any()) }
        verify { recipientsInteractor.deleteItem(eq(123)) }
    }

    @Test
    fun createRecipient_callCreateOnInteractor() {
        // when
        viewModel.createNewRecipient()

        // then
        verify(exactly = 1) { recipientsInteractor.createNewItem() }
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun save_callSaveOnInteractor() {
        // when
        viewModel.save()
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        coVerify(exactly = 1) { recipientsInteractor.save() }
        coVerify(exactly = 1) { settingsInteractor.save() }
    }

    @Test
    fun init_callResetOnRecipientsInteractorAndReadFromSettingsRepository() {
        // when
        viewModel

        // then
        coVerify(exactly = 1) { recipientsInteractor.reset() }
        coVerify(exactly = 1) { settingsInteractor.reset() }
    }

    @Test
    fun updateLogin_updateUiState() {
        // when
        viewModel.updateLogin("newLogin")

        // then
        verify(exactly = 1) { settingsInteractor.updateLogin(any()) }
        verifyNotSaved()
    }

    @Test
    fun updatePassword_updateUiState() {
        // when
        viewModel.updatePassword("newPassword")

        // then
        verify(exactly = 1) { settingsInteractor.updatePassword(any()) }
        verifyNotSaved()
    }

    @Test
    fun updateHost_updateUiState() {
        // when
        viewModel.updateHost("newHost")

        // then
        verify(exactly = 1) { settingsInteractor.updateHost(any()) }
        verifyNotSaved()
    }

    @Test
    fun updatePort_updateUiState() {
        // when
        viewModel.updatePort("123")

        // then
        verify(exactly = 1) { settingsInteractor.updatePort(any()) }
        verifyNotSaved()
    }

    @Test
    fun toggleReceiverStatus_updateUiStateAndSaveSettings() {
        // when
        viewModel.toggleReceiverStatus()

        // then
        coVerify(exactly = 1) { settingsInteractor.toggleReceiverStatus() }
        coVerify(exactly = 0) { recipientsInteractor.save() }
    }

    @Test
    fun updateRecipient_callUpdateOnRecipientsInteractor() {
        // when
        viewModel.updateRecipient(124, "332")

        // then
        verify(exactly = 1) { recipientsInteractor.updateItem(any(), any()) }
        verify { recipientsInteractor.updateItem(eq(124), eq(Recipient(id = 124, value = "332"))) }
        verifyNotSaved()
    }
}