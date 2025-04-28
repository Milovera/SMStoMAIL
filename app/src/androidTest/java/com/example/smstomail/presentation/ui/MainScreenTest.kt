package com.example.smstomail.presentation.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smstomail.R
import com.example.smstomail.presentation.ui.state.PermissionsUiState
import com.example.smstomail.presentation.ui.state.SettingsUiState
import com.example.smstomail.presentation.ui.state.SettingsUiStateData
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private var settingsUiStateMock = MutableStateFlow(
        SettingsUiState(
            SettingsUiStateData(
                isReceiverEnabled = false
            ),
            isValid = true,
            isModified = false
        )
    )
    private var permissionsUiStateMock = MutableStateFlow(
        PermissionsUiState(
            isSMSReceiveAllowed = false,
            isNotificationAllowed = false
        )
    )
    private var onReceiverToggleButtonClickedCounter: Int = 0

    @Before
    fun setup() {
        composeTestRule.setContent {
            val settingsUiState by settingsUiStateMock.asStateFlow().collectAsState()
            val permissionsUiState by permissionsUiStateMock.asStateFlow().collectAsState()

            MainScreen(
                settingsUiState = settingsUiState,
                permissionsUiState = permissionsUiState,
                onReceiverToggleStatusButtonClicked = {
                    onReceiverToggleButtonClickedCounter++
                    settingsUiStateMock.value = settingsUiStateMock.value.copy(
                        item = settingsUiStateMock.value.item.copy(
                            isReceiverEnabled = !settingsUiStateMock.value.item.isReceiverEnabled
                        )
                    )
                }
            )
        }
    }

    @Test
    fun noPermissions_requestPermissionsButtonAvailable() {
        // given
        permissionsUiStateMock.value = PermissionsUiState(
            isSMSReceiveAllowed = false,
            isNotificationAllowed = false
        )

        // when
        composeTestRule.waitForIdle()

        // then
        composeTestRule
            .onNodeWithStringId(R.string.permissions_request_button_text)
            .assertIsDisplayed()
    }

    @Test
    fun noSMSPermissions_requestPermissionsButtonAvailable() {
        // given
        permissionsUiStateMock.value = PermissionsUiState(
            isSMSReceiveAllowed = false,
            isNotificationAllowed = true
        )

        // when
        composeTestRule.waitForIdle()

        // then
        composeTestRule
            .onNodeWithStringId(R.string.permissions_request_button_text)
            .assertIsDisplayed()
    }

    @Test
    fun noNotificationPermissions_requestPermissionsButtonAvailable() {
        // given
        permissionsUiStateMock.value = PermissionsUiState(
            isSMSReceiveAllowed = true,
            isNotificationAllowed = false
        )

        // when
        composeTestRule.waitForIdle()

        // then
        composeTestRule
            .onNodeWithStringId(R.string.permissions_request_button_text)
            .assertIsDisplayed()
    }

    @Test
    fun gotPermissions_toggleReceiverButtonAvailable() {
        // given
        permissionsUiStateMock.value = PermissionsUiState(
            isSMSReceiveAllowed = true,
            isNotificationAllowed = true
        )

        // when
        composeTestRule.waitForIdle()

        // then
        composeTestRule
            .onNodeWithStringId(R.string.receiver_disabled_text)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithStringId(R.string.enable_text)
            .assertIsDisplayed()
    }

    @Test
    fun toggleReceiverToEnabledState_receiverEnabled() {
        // given
        permissionsUiStateMock.value = PermissionsUiState(
            isSMSReceiveAllowed = true,
            isNotificationAllowed = true
        )

        // when
        composeTestRule
            .onNodeWithStringId(R.string.enable_text)
            .performClick()

        // then
        composeTestRule
            .onNodeWithStringId(R.string.receiver_enabled_text)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithStringId(R.string.disable_text)
            .assertIsDisplayed()
    }

    @Test
    fun toggleReceiverToDisabledState_receiverDisabled() {
        // given
        permissionsUiStateMock.value = PermissionsUiState(
            isSMSReceiveAllowed = true,
            isNotificationAllowed = true
        )
        settingsUiStateMock.value = SettingsUiState(
            item = SettingsUiStateData(
                isReceiverEnabled = true
            ),
            isValid = true
        )
        composeTestRule.waitForIdle()

        // when
        composeTestRule
            .onNodeWithStringId(R.string.disable_text)
            .performClick()

        // then
        composeTestRule
            .onNodeWithStringId(R.string.receiver_disabled_text)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithStringId(R.string.enable_text)
            .assertIsDisplayed()
    }

    @Test
    fun invalidSettings_receiverToggleUnavailable() {
        // given
        permissionsUiStateMock.value = PermissionsUiState(
            isSMSReceiveAllowed = true,
            isNotificationAllowed = true
        )
        settingsUiStateMock.value = SettingsUiState()

        // then
        composeTestRule
            .onNodeWithStringId(R.string.disable_text)
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithStringId(R.string.enable_text)
            .assertIsNotDisplayed()
    }

    @Test
    fun invalidSettingsWithEnabledReceiver_toggleReceiverStatusCalled() {
        // given
        settingsUiStateMock.value = SettingsUiState(
            item = SettingsUiStateData(
                isReceiverEnabled = true
            ),
            isValid = false
        )

        // when
        composeTestRule.waitForIdle()

        // then
        assertTrue(
            onReceiverToggleButtonClickedCounter == 1
        )
    }
}