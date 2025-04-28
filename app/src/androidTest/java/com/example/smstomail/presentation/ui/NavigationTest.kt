package com.example.smstomail.presentation.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.compose.runtime.getValue
import com.example.smstomail.R
import com.example.smstomail.presentation.models.PermissionsViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {
    companion object {
        const val BACK_BUTTON_CONTENT_DESCRIPTION = "Back"
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var navController: TestNavHostController

    @Before
    fun setupNavHost() {
        val permissionsViewModel = PermissionsViewModel(composeTestRule.activity.application)

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            val permissionsUiState by permissionsViewModel.uiState.collectAsState()
            AppScreen(
                permissionsUiState = permissionsUiState,
                navController = navController
            )
        }
    }

    private fun navigateToSettingsScreen() {
        composeTestRule.onNodeWithStringId(R.string.settings_text)
            .performClick()
    }

    private fun navigateToFiltersScreen() {
        navigateToSettingsScreen()
        composeTestRule.onNodeWithStringId(R.string.filters_text)
            .performClick()
    }

    private fun navigateUp() {
        composeTestRule.onNodeWithContentDescription(BACK_BUTTON_CONTENT_DESCRIPTION)
            .performClick()
    }

    @Test
    fun clickSettingsButton_navigatesToSettingsScreen() {
        // when
        navigateToSettingsScreen()

        // then
        navController.assertCurrentRouteName(AppScreenTypes.SETTINGS.name)
        composeTestRule
            .onNodeWithStringId(R.string.settings_text)
            .assertIsDisplayed()
    }

    @Test
    fun clickFiltersButton_navigatesToFiltersScreen() {
        // when
        navigateToFiltersScreen()

        // then
        navController.assertCurrentRouteName(AppScreenTypes.FILTERS.name)
        composeTestRule
            .onNodeWithStringId(R.string.filters_text)
            .assertIsDisplayed()
    }

    @Test
    fun clickBackButtonOnFiltersScreen_navigatesToSettingsScreen() {
        // when
        navigateToFiltersScreen()
        navigateUp()

        // then
        navController.assertCurrentRouteName(AppScreenTypes.SETTINGS.name)
        composeTestRule
            .onNodeWithStringId(R.string.settings_text)
            .assertIsDisplayed()
    }

    @Test
    fun clickBackButtonOnSettingsScreen_navigatesToMainScreen() {
        // when
        navigateToSettingsScreen()
        navigateUp()

        // then
        navController.assertCurrentRouteName(AppScreenTypes.MAIN.name)
        composeTestRule
            .onNodeWithStringId(R.string.app_name)
            .assertIsDisplayed()
    }
}