package com.example.smstomail.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.smstomail.R
import com.example.smstomail.presentation.ui.state.PermissionsUiState
import com.example.smstomail.presentation.ui.state.SettingsUiState
import com.example.smstomail.presentation.ui.state.SettingsUiStateData
import com.example.smstomail.presentation.ui.theme.AppTheme

@Composable
fun MainScreen(
    settingsUiState: SettingsUiState,
    permissionsUiState: PermissionsUiState,
    modifier: Modifier = Modifier,
    onReceiverToggleStatusButtonClicked: () -> Unit = {},
    onPermissionsRequestButtonClicked: () -> Unit = {},
    onSettingsButtonClicked: () -> Unit = {}
) {
    // выключаем ресивер если он был включен с некорректными настройками
    if(!settingsUiState.isValid && settingsUiState.item.isReceiverEnabled) {
        onReceiverToggleStatusButtonClicked()
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // есть пермиссии - даем возможность включать и выключать ресивер
        if(permissionsUiState.isSMSReceiveAllowed && permissionsUiState.isNotificationAllowed) {
            Text(
                text = stringResource(
                    if(settingsUiState.item.isReceiverEnabled) {
                        R.string.receiver_enabled_text
                    } else {
                        R.string.receiver_disabled_text
                    }
                ),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    bottom = dimensionResource(R.dimen.padding_medium)
                )
            )
            //
            if(settingsUiState.isValid) {
                Button(
                    onClick = onReceiverToggleStatusButtonClicked
                ) {
                    Text(
                        text = stringResource(
                            if (settingsUiState.item.isReceiverEnabled) {
                                R.string.disable_text
                            } else {
                                R.string.enable_text
                            }
                        )
                    )
                }
            }
        }
        // запрашиваем разрешения
        else {
            Text(
                text = stringResource(
                    if(permissionsUiState.isNotificationAllowed) {
                        R.string.permissions_sms_denied_text
                    } else {
                        R.string.permissions_notifications_denied_text
                    }
                ),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_medium))
            )
            Button(
                onClick = onPermissionsRequestButtonClicked
            ) {
                Text(
                    text = stringResource(R.string.permissions_request_button_text)
                )
            }
        }
        // кнопка для перехода в настройки приложения
        Button(
            onClick = onSettingsButtonClicked
        ) {
            Text(
                text = stringResource(R.string.settings_text)
            )
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewMainScreen() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            MainScreen(
                settingsUiState = SettingsUiState(
                    item = SettingsUiStateData(),
                    isValid = true,
                    isModified = false
                ),
                permissionsUiState = PermissionsUiState(
                    isSMSReceiveAllowed = true,
                    isNotificationAllowed = true
                )
            )
        }
    }
}