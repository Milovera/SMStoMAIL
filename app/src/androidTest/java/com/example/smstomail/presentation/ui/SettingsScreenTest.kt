package com.example.smstomail.presentation.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smstomail.R
import com.example.smstomail.data.entity.Recipient
import com.example.smstomail.data.entity.SettingsData
import com.example.smstomail.data.repository.IRecipientsRepository
import com.example.smstomail.data.repository.ISettingsRepository
import com.example.smstomail.domain.interactors.RecipientsInteractor
import com.example.smstomail.domain.interactors.SettingsInteractor
import com.example.smstomail.presentation.models.SettingsViewModel
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {
    companion object {
        const val REMOVE_RECIPIENT_BUTTON_TAG = "removeRecipientButton"
        const val ADD_RECIPIENT_BUTTON_TAG = "addRecipientButton"
        const val RECIPIENT_TEXT_FIELD_TAG = "recipientEditTag"
    }
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val settingsRepositoryMock = object: ISettingsRepository {
        var savedData = SettingsData(
            login = "login",
            password = "password",
            host = "host.com",
            sslPort = 445,
            isReceiverEnabled = false
        )

        override fun read() = savedData
        override fun write(data: SettingsData) { savedData = data }
    }
    private val recipientsRepositoryMock = object: IRecipientsRepository {
        val dataMap: MutableMap<Int, Recipient> = mutableMapOf(
            0 to Recipient(0, "recipient@you.me")
        )

        override suspend fun getItems(): List<Recipient> {
            return dataMap.values.toList()
        }

        override suspend fun insertItem(item: Recipient) {
            dataMap[item.id] = item
        }

        override suspend fun updateItem(item: Recipient) = insertItem(item)

        override suspend fun deleteItem(itemId: Int) {
            dataMap.remove(itemId)
        }
    }

    private val settingsViewModel = SettingsViewModel(
        settingsInteractor = SettingsInteractor(
            settingsRepository = settingsRepositoryMock
        ),
        recipientsInteractor = RecipientsInteractor(
            recipientsRepository = recipientsRepositoryMock
        )
    )

    private var onSaveButtonClickedCounter = 0
    private var onResetButtonClickedCounter = 0

    @Before
    fun setup() {
        composeTestRule.setContent {
            val settingsUiState by settingsViewModel.settingsUiState.collectAsState()

            SettingsScreen(
                settingsUiState = settingsUiState,
                onUpdateLogin = settingsViewModel::updateLogin,
                onUpdatePassword = settingsViewModel::updatePassword,
                onUpdateHost = settingsViewModel::updateHost,
                onUpdatePort = settingsViewModel::updatePort,
                onCreateRecipient = settingsViewModel::createNewRecipient,
                onRecipientUpdate = settingsViewModel::updateRecipient,
                onDeleteRecipient = settingsViewModel::deleteRecipient,
                onSaveButtonClicked = { onSaveButtonClickedCounter++ },
                onResetButtonClicked = { onResetButtonClickedCounter++; settingsViewModel.reset() }
            )
        }
    }

    @Test
    fun openSettings_applyButtonDisabled() {
        // when
        // initial settingsViewModel values already valid

        // then
        composeTestRule
            .onNodeWithStringId(R.string.apply_settings_button_text)
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun saveButtonClicked_viewModelSaveTriggered() {
        // when
        composeTestRule
            .onNodeWithTagStringId(R.string.login_label)
            .performTextClearance()
        composeTestRule
            .onNodeWithTagStringId(R.string.login_label)
            .performTextInput("newLogin")

        composeTestRule.waitForIdle()

        // then
        composeTestRule
            .onNodeWithStringId(R.string.apply_settings_button_text)
            .performClick()

        assertTrue(
            onSaveButtonClickedCounter == 1
        )
        assertTrue(
            onResetButtonClickedCounter == 0
        )
    }

    @Test
    fun resetButtonClicked_valuesRestored() {
        // given
        composeTestRule
            .onNodeWithTagStringId(R.string.login_label)
            .performTextClearance()

        composeTestRule
            .onNodeWithTagStringId(R.string.password_label)
            .performTextClearance()

        composeTestRule
            .onNodeWithTagStringId(R.string.smtp_server_label)
            .performTextClearance()

        composeTestRule
            .onNodeWithTagStringId(R.string.ssl_port_label)
            .performTextClearance()

        composeTestRule
            .onNodeWithTag(RECIPIENT_TEXT_FIELD_TAG)
            .performTextClearance()

        // when
        composeTestRule
            .onNodeWithStringId(R.string.reset_settings_button_text)
            .performClick()

        composeTestRule.waitForIdle()

        // then
        assertTrue(
            onSaveButtonClickedCounter == 0
        )
        assertTrue(
            onResetButtonClickedCounter == 1
        )

        composeTestRule
            .onNodeWithTagStringId(R.string.login_label)
            .assertTextEquals(settingsRepositoryMock.savedData.login)

        composeTestRule
            .onNodeWithTagStringId(R.string.password_label)
            .fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.EditableText)
            ?.text
            .let { password ->
                assertTrue(password?.length == settingsRepositoryMock.savedData.password.length)
            }

        composeTestRule
            .onNodeWithTagStringId(R.string.smtp_server_label)
            .assertTextEquals(settingsRepositoryMock.savedData.host)

        composeTestRule
            .onNodeWithTagStringId(R.string.ssl_port_label)
            .assertTextEquals(settingsRepositoryMock.savedData.sslPort.toString())

        composeTestRule
            .onNodeWithTag(RECIPIENT_TEXT_FIELD_TAG)
            .assertTextEquals(recipientsRepositoryMock.dataMap.values.first().value)
    }

    @Test
    fun emptyLogin_applyButtonDisabled() {
        // when
        composeTestRule
            .onNodeWithTagStringId(R.string.login_label)
            .performTextClearance()

        //then
        composeTestRule
            .onNodeWithStringId(R.string.apply_settings_button_text)
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun validLoginInput_applyButtonEnabled() {
        // given
        composeTestRule
            .onNodeWithTagStringId(R.string.login_label)
            .performTextClearance()

        // when
        composeTestRule
            .onNodeWithTagStringId(R.string.login_label)
            .performTextInput("newLogin")
        composeTestRule.waitForIdle()

        //then
        composeTestRule
            .onNodeWithStringId(R.string.apply_settings_button_text)
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun emptyPassword_applyButtonDisabled() {
        // when
        composeTestRule
            .onNodeWithTagStringId(R.string.password_label)
            .performTextClearance()

        //then
        composeTestRule
            .onNodeWithStringId(R.string.apply_settings_button_text)
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun validPasswordInput_applyButtonEnabled() {
        // given
        composeTestRule
            .onNodeWithTagStringId(R.string.password_label)
            .performTextClearance()

        // when
        composeTestRule
            .onNodeWithTagStringId(R.string.password_label)
            .performTextInput("newPassword")
        composeTestRule.waitForIdle()

        //then
        composeTestRule
            .onNodeWithStringId(R.string.apply_settings_button_text)
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun emptyServer_applyButtonDisabled() {
        // when
        composeTestRule
            .onNodeWithTagStringId(R.string.smtp_server_label)
            .performTextClearance()

        //then
        composeTestRule
            .onNodeWithStringId(R.string.apply_settings_button_text)
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun validServerInput_applyButtonEnabled() {
        // given
        composeTestRule
            .onNodeWithTagStringId(R.string.smtp_server_label)
            .performTextClearance()

        // when
        composeTestRule
            .onNodeWithTagStringId(R.string.smtp_server_label)
            .performTextInput("newHost.com")
        composeTestRule.waitForIdle()

        //then
        composeTestRule
            .onNodeWithStringId(R.string.apply_settings_button_text)
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun emptyPort_applyButtonDisabled() {
        // when
        composeTestRule
            .onNodeWithTagStringId(R.string.ssl_port_label)
            .performTextClearance()

        //then
        composeTestRule
            .onNodeWithStringId(R.string.apply_settings_button_text)
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun validPort_applyButtonEnabled() {
        // given
        composeTestRule
            .onNodeWithTagStringId(R.string.ssl_port_label)
            .performTextClearance()

        // when
        composeTestRule
            .onNodeWithTagStringId(R.string.ssl_port_label)
            .performTextInput("777")

        //then
        composeTestRule
            .onNodeWithStringId(R.string.apply_settings_button_text)
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun oneRecipient_removeRecipientButtonNotDisplayed() {
        // when
        // initial settingsViewModel values already valid

        //then
        composeTestRule
            .onNodeWithTag(REMOVE_RECIPIENT_BUTTON_TAG)
            .assertIsNotDisplayed()
    }

    @Test
    fun twoRecipients_removeRecipientButtonDisplayed() {
        // when
        composeTestRule
            .onNodeWithTag(ADD_RECIPIENT_BUTTON_TAG)
            .performClick()

        //then
        val removeButtonCount = composeTestRule
            .onAllNodesWithTag(REMOVE_RECIPIENT_BUTTON_TAG)
            .fetchSemanticsNodes()
            .size

        assertTrue(removeButtonCount == 2)
    }

    @Test
    fun newRecipient_applyButtonDisabled() {
        // when
        composeTestRule
            .onNodeWithTag(ADD_RECIPIENT_BUTTON_TAG)
            .performClick()

        //then
        composeTestRule
            .onNodeWithStringId(R.string.apply_settings_button_text)
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun newRecipientFilled_applyButtonEnabled() {
        // given
        composeTestRule
            .onNodeWithTag(ADD_RECIPIENT_BUTTON_TAG)
            .performClick()

        composeTestRule.waitForIdle()

        // when
        composeTestRule
            .onNode(
                hasTestTag(RECIPIENT_TEXT_FIELD_TAG) and hasText("")
            )
            .performTextInput("valid@mail.com")

        composeTestRule.waitForIdle()

        //then
        composeTestRule
            .onNodeWithStringId(R.string.apply_settings_button_text)
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun deleteOneOfTwoRecipient_deleteRecipientButtonHiding() {
        // given
        composeTestRule
            .onNodeWithTag(ADD_RECIPIENT_BUTTON_TAG)
            .performClick()

        composeTestRule.waitForIdle()

        // when
        composeTestRule
            .onAllNodesWithTag(REMOVE_RECIPIENT_BUTTON_TAG)
            .onFirst()
            .performClick()

        composeTestRule.waitForIdle()

        // then
        composeTestRule
            .onNodeWithTag(REMOVE_RECIPIENT_BUTTON_TAG)
            .assertIsNotDisplayed()
    }
}