package com.example.smstomail.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smstomail.presentation.models.SettingsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.smstomail.R
import com.example.smstomail.data.entity.Message
import com.example.smstomail.domain.workers.MailSenderWorker
import com.example.smstomail.presentation.models.FiltersViewModel
import com.example.smstomail.presentation.ui.state.PermissionsUiState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

enum class AppScreenTypes {
    MAIN,
    SETTINGS,
    FILTERS
}

@Composable
fun AppScreen(
    permissionsUiState: PermissionsUiState,
    settingsViewModel: SettingsViewModel,
    filtersViewModel: FiltersViewModel,
    onPermissionsRequestClicked: () -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreenTypes.valueOf(
        backStackEntry?.destination?.route ?: AppScreenTypes.MAIN.name
    )
    val currentScreenTitle = when(currentScreen) {
        AppScreenTypes.MAIN -> stringResource(R.string.app_name)
        AppScreenTypes.SETTINGS -> stringResource(R.string.settings_text)
        AppScreenTypes.FILTERS -> stringResource(R.string.filters_text)
    }

    val settingsUiState by settingsViewModel.settingsUiState.collectAsState()
    val filtersUiState by filtersViewModel.filtersUiState.collectAsState()

    Scaffold(
        topBar = {
            AppBar(
                title = currentScreenTitle,
                canNavigateBack = currentScreen != AppScreenTypes.MAIN,
                navigateUp = {
                    navController.navigateUp()
                }
            )
        }
    )   { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppScreenTypes.MAIN.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(route = AppScreenTypes.MAIN.name) {
                    MainScreen(
                        settingsUiState = settingsUiState,
                        permissionsUiState = permissionsUiState,
                        onSettingsButtonClicked = {
                            settingsViewModel.reset()
                            navController.navigate(AppScreenTypes.SETTINGS.name)
                        },
                        onPermissionsRequestButtonClicked = onPermissionsRequestClicked,
                        onReceiverToggleStatusButtonClicked = settingsViewModel::toggleReceiverStatus,
                        modifier = Modifier
                            .padding(
                                all = dimensionResource(R.dimen.padding_medium)
                            )
                    )
                }
                composable(route = AppScreenTypes.SETTINGS.name) {
                    SettingsScreen(
                        settingsUiState = settingsUiState,
                        onUpdateLogin = settingsViewModel::updateLogin,
                        onUpdatePassword = settingsViewModel::updatePassword,
                        onUpdateHost = settingsViewModel::updateHost,
                        onUpdatePort = settingsViewModel::updatePort,
                        onCreateRecipient = settingsViewModel::createNewRecipient,
                        onRecipientUpdate = settingsViewModel::updateRecipient,
                        onDeleteRecipient = settingsViewModel::deleteRecipient,
                        onFilterSettingsButtonClicked = {
                            navController.navigate(AppScreenTypes.FILTERS.name)
                        },
                        onResetButtonClicked = settingsViewModel::reset,
                        onSaveButtonClicked = {
                            settingsViewModel.save()

                            lifecycleOwner.lifecycleScope.launch {
                                val recipient = settingsUiState.item.recipientsList.first()
                                val message = Message(
                                    sender = context.getString(R.string.test_mail_title),
                                    body = context.getString(R.string.test_mail_body)
                                )
                                val workId = MailSenderWorker.createRequest(
                                    message = message,
                                    recipient = recipient.value
                                ).let { workRequest ->
                                    WorkManager.getInstance(context).enqueue(workRequest)
                                    workRequest.id
                                }

                                WorkManager
                                    .getInstance(context)
                                    .getWorkInfoByIdFlow(workId)
                                    .filterNotNull()
                                    .collect { workInfo ->
                                        // при фейле или успехе показываю тост и завершаю отслеживание статуса
                                        when (workInfo.state) {
                                            WorkInfo.State.SUCCEEDED ->  R.string.toast_test_email_successful
                                            WorkInfo.State.FAILED -> R.string.toast_test_email_fail
                                            else -> null
                                        }
                                            ?.let { textId ->
                                                Toast.makeText(context, textId, Toast.LENGTH_SHORT).show()
                                                cancel()
                                            }
                                    }
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                all = dimensionResource(R.dimen.padding_small)
                            )
                    )
                }
                composable(route = AppScreenTypes.FILTERS.name) {
                    FiltersScreen(
                        filtersUiState = filtersUiState,
                        onAddButtonClicked = filtersViewModel::createNewFilter,
                        onResetButtonClicked = filtersViewModel::reset,
                        onRemoveButtonClicked = filtersViewModel::deleteFilter,
                        onUpdateItem = filtersViewModel::updateFilter,
                        onSaveButtonClicked = filtersViewModel::save,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                all = dimensionResource(R.dimen.padding_small)
                            )
                    )
                }
            }
        }
}