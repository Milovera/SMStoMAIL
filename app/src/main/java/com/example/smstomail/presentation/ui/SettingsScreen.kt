package com.example.smstomail.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import com.example.smstomail.R
import com.example.smstomail.data.entity.Recipient
import com.example.smstomail.presentation.ui.components.EditTextSection
import com.example.smstomail.presentation.ui.components.RecipientEdit
import com.example.smstomail.presentation.ui.components.SectionDivider
import com.example.smstomail.presentation.ui.state.SettingsUiState
import com.example.smstomail.presentation.ui.state.SettingsUiStateData
import com.example.smstomail.presentation.ui.theme.AppTheme

@Composable
fun SettingsScreen(
    settingsUiState: SettingsUiState,
    modifier: Modifier = Modifier,
    onUpdateLogin: (String) -> Unit = {},
    onUpdatePassword: (String) -> Unit = {},
    onUpdateHost: (String) -> Unit = {},
    onUpdatePort: (String) -> Unit = {},
    onCreateRecipient: () -> Unit = {},
    onRecipientUpdate: (Int, String) -> Unit = {_, _ -> },
    onDeleteRecipient: (Int) -> Unit = {},
    onFilterSettingsButtonClicked: () -> Unit = {},
    onResetButtonClicked: () -> Unit = {},
    onSaveButtonClicked: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
        ) {
            item {
                SectionDivider(
                    sectionTitle = R.string.sender_section_text
                )
            }
            item {
                EditTextSection(
                    labelText = R.string.login_label,
                    value = settingsUiState.item.login,
                    onValueChange = onUpdateLogin,
                    modifier = Modifier
                        .padding(
                            top = dimensionResource(R.dimen.padding_small),
                            bottom = dimensionResource(R.dimen.padding_small)
                        )
                )
            }
            item {
                EditTextSection(
                    labelText = R.string.password_label,
                    value = settingsUiState.item.password,
                    onValueChange = onUpdatePassword,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(
                            bottom = dimensionResource(R.dimen.padding_small)
                        )
                ) {
                    EditTextSection(
                        labelText = R.string.smtp_server_label,
                        value = settingsUiState.item.host,
                        onValueChange = onUpdateHost,
                        modifier = Modifier.weight(4f)
                    )
                    EditTextSection(
                        labelText = R.string.ssl_port_label,
                        value = settingsUiState.item.sslPort,
                        onValueChange = onUpdatePort,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.weight(3f)
                    )
                }
            }
            item {
                SectionDivider(
                    R.string.other_section_text
                )
            }
            item {
                Text(
                    text = stringResource(R.string.recipients_label),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            all = dimensionResource(R.dimen.padding_small)
                        )
                )
            }
            val isLastRecipient = settingsUiState.item.recipientsList.size == 1
            itemsIndexed(settingsUiState.item.recipientsList) { index, item ->
                RecipientEdit(
                    value = item.value,
                    onValueChange = { newValue ->
                        onRecipientUpdate(item.id, newValue)
                    },
                    isValueCorrect = {
                        Recipient.emailRegex.matches(it)
                    },
                    onAddButtonClicked = onCreateRecipient,
                    onRemoveButtonClicked = {
                        onDeleteRecipient(item.id)
                    },
                    isLast = isLastRecipient,
                    modifier = Modifier
                        .padding(bottom = dimensionResource(R.dimen.padding_small))
                        .fillMaxWidth()
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(R.dimen.padding_large),
                    start = dimensionResource(R.dimen.padding_medium),
                    end = dimensionResource(R.dimen.padding_medium),
                    bottom = dimensionResource(R.dimen.padding_large)
                )
        ) {
            Button(
                onClick = onResetButtonClicked,
                enabled = settingsUiState.isModified
            ) {
                Text(
                    text = stringResource(R.string.reset_settings_button_text)
                )
            }
            Button(
                onClick = onFilterSettingsButtonClicked
            ) {
                Text(
                    text = stringResource(R.string.filters_text)
                )
            }
            Button(
                onClick = onSaveButtonClicked,
                enabled = settingsUiState.isValid && settingsUiState.isModified
            ) {
                Text(
                    text = stringResource(R.string.apply_settings_button_text)
                )
            }
        }
    }
}

@PreviewLightDark()
@Composable
fun SettingsScreenPreview() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            SettingsScreen(
                settingsUiState = SettingsUiState(
                    item = SettingsUiStateData(
                        login = "Login",
                        password = "passwd",
                        host = "someHost.com",
                        sslPort = "443",
                        recipientsList = listOf(
                            Recipient(1, "valid@email.com")
                        )
                    ),
                    isValid = true,
                    isModified = true
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        all = dimensionResource(R.dimen.padding_small)
                    )
            )
        }
    }
}