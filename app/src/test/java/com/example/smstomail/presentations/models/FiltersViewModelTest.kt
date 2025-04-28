package com.example.smstomail.presentations.models

import com.example.smstomail.data.entity.Filter
import com.example.smstomail.data.entity.ItemSnapshot
import com.example.smstomail.data.repository.mockAndroidLog
import com.example.smstomail.data.repository.unmockAndroidLog
import com.example.smstomail.domain.interactors.FiltersInteractor
import com.example.smstomail.presentation.models.FiltersViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

class FiltersViewModelTest {
    private val filtersFlow = MutableStateFlow< ItemSnapshot<List<Filter>>>(ItemSnapshot<List<Filter>>(emptyList(), false, false))
    private val interactor = mockk<FiltersInteractor> {
        coEvery { reset() } returns Unit
        coEvery { save() } returns Unit
        every { createNewItem() } returns Unit
        every { deleteItem(any()) } returns Unit
        every { itemsStateFlow } returns filtersFlow.asStateFlow()
        every { updateItem(any(), any()) } returns Unit
    }
    private val viewModel by lazy {
        FiltersViewModel(interactor)
    }

    @BeforeEach
    fun setUp() {
        mockAndroidLog()
    }

    @AfterEach
    fun tearDown() {
        unmockAndroidLog()
    }

    @Test
    fun init_callResetOnInteractor() {
        // when
        viewModel

        // then
        coVerify(exactly = 1) { interactor.reset() }
    }

    @Test
    fun reset_callResetOnInteractor() {
        // when
        viewModel.reset()

        // then
        coVerify(exactly = 2) { interactor.reset() }
    }

    @Test
    fun delete_callDeleteOnInteractor() {
        // when
        viewModel.deleteFilter(123)

        // then
        verify(exactly = 1) { interactor.deleteItem(any()) }
        verify { interactor.deleteItem(eq(123)) }
    }

    @Test
    fun create_callCreateOnInteractor() {
        // when
        viewModel.createNewFilter()

        // then
        verify(exactly = 1) { interactor.createNewItem() }
    }

    @Test
    fun save_callSaveOnInteractor() {
        // when
        viewModel.save()

        // then
        coVerify(exactly = 1) { interactor.save() }
    }

    @Test
    fun update_callUpdateOnInteractor() {
        // when
        viewModel.updateFilter(
            filterId = 1234,
            filterType = Filter.Type.SenderExclude,
            value = "4321"
        )

        // then
        verify { interactor.updateItem(eq(1234), eq(Filter(id = 1234, type = Filter.Type.SenderExclude, value = "4321"))) }
    }
}