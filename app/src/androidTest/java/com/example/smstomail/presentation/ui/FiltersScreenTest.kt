package com.example.smstomail.presentation.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smstomail.R
import com.example.smstomail.data.entity.Filter
import com.example.smstomail.data.repository.IFiltersRepository
import com.example.smstomail.domain.interactors.FiltersInteractor
import com.example.smstomail.presentation.models.FiltersViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FiltersScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val filtersRepositoryMock = object: IFiltersRepository {
        val items = mutableListOf(
                Filter(0, Filter.Type.MessageInclude, "gg")
            )
        override suspend fun getItems(): List<Filter>  = items

        override suspend fun insertItem(item: Filter) {
            items.plus(item)
        }

        override suspend fun updateItem(item: Filter) {
            items.removeIf {it.id == item.id }
            items.add(item)
        }

        override suspend fun deleteItem(itemId: Int) {
            items.removeIf {it.id == itemId }
        }
    }

    private val filtersViewModel = FiltersViewModel(
        filtersInteractor = FiltersInteractor(
            filtersRepository = filtersRepositoryMock
        )
    )

    private var onSaveButtonClickedCounter = 0
    private var onResetButtonClickedCounter = 0

    @Before
    fun setup() {
        composeTestRule.setContent {
            val filtersUiState by filtersViewModel.filtersUiState.collectAsState()

            FiltersScreen(
                filtersUiState = filtersUiState,
                onAddButtonClicked = filtersViewModel::createNewFilter,
                onUpdateItem = filtersViewModel::updateFilter,
                onRemoveButtonClicked = filtersViewModel::deleteFilter,
                onResetButtonClicked = { onResetButtonClickedCounter++; filtersViewModel.reset()},
                onSaveButtonClicked = { onSaveButtonClickedCounter++; filtersViewModel.save() }
            )
        }
    }

    @Test
    fun addButtonClicked_createdNewFilter() {
        // when
        composeTestRule
            .onNodeWithStringId(R.string.add_button_text)
            .performClick()

        // then
        val filtersCount = composeTestRule
            .onAllNodesWithTag("removeFilterButton")
            .fetchSemanticsNodes()
            .size

        assert(filtersCount == 2)

        composeTestRule
            .onNodeWithStringId(R.string.save_button_text)
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun fillingFilterText_enabledSaveButton() {
        // given
        composeTestRule
            .onNodeWithStringId(R.string.add_button_text)
            .performClick()

        // when
        composeTestRule
            .onNode(
                hasTestTag("filterTextField") and hasText("")
            )
            .performTextInput("filter")

        // then
        composeTestRule
            .onNodeWithStringId(R.string.save_button_text)
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun emptyFilterText_disabledSaveButton() {
        // given
        composeTestRule
            .onNodeWithStringId(R.string.add_button_text)
            .performClick()

        // then
        composeTestRule
            .onNodeWithStringId(R.string.save_button_text)
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun resetButtonClicked_resetFilters() {
        // given
        composeTestRule
            .onNodeWithStringId(R.string.add_button_text)
            .performClick()

        composeTestRule.waitForIdle()

        // when
        composeTestRule
            .onNodeWithStringId(R.string.reset_settings_button_text)
            .performClick()

        // then
        val filtersCount = composeTestRule
            .onAllNodesWithTag("removeFilterButton")
            .fetchSemanticsNodes()
            .size

        assert(filtersCount == 1)
        assert(onSaveButtonClickedCounter == 0)
        assert(onResetButtonClickedCounter == 1)

        composeTestRule
            .onNodeWithStringId(R.string.save_button_text)
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun saveButtonClicked_callSaveOnViewModel() {
        // given
        composeTestRule
            .onNodeWithStringId(R.string.add_button_text)
            .performClick()

        composeTestRule
            .onNode(
                hasTestTag("filterTextField") and hasText("")
            )
            .performTextInput("filter")

        // when
        composeTestRule
            .onNodeWithStringId(R.string.save_button_text)
            .performClick()

        // then
        assert(onSaveButtonClickedCounter == 1)
        assert(onResetButtonClickedCounter == 0)
    }

    @Test
    fun deleteLastFilter_emptyFilterList() {
        // when
        composeTestRule
            .onNodeWithTag("removeFilterButton")
            .performClick()

        // then
        val filtersCount = composeTestRule
            .onAllNodesWithTag("removeFilterButton")
            .fetchSemanticsNodes()
            .size

        assert(filtersCount == 0)
    }

    @Test
    fun deleteOneOfTwoFilters_deleteCorrectFilter() {
        // given
        composeTestRule
            .onNodeWithStringId(R.string.add_button_text)
            .performClick()

        // when
        composeTestRule
            .onAllNodesWithTag("removeFilterButton")
            .onLast()
            .performClick()

        // then
        composeTestRule
            .onNode(
                hasTestTag("filterTextField") and hasText("gg")
            )
            .assertIsDisplayed()
    }
}