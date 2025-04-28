package com.example.smstomail

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.getValue
import com.example.smstomail.presentation.models.AppViewModelFactory
import com.example.smstomail.presentation.models.PermissionsViewModel
import com.example.smstomail.presentation.ui.AppScreen
import com.example.smstomail.presentation.ui.theme.AppTheme
import androidx.core.net.toUri

class MainActivity: ComponentActivity() {
    private lateinit var permissionsViewModel: PermissionsViewModel

    private val permissionRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionsViewModel.checkPermissions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        permissionsViewModel = ViewModelProvider(this, AppViewModelFactory.Factory).get(PermissionsViewModel::class.java)

        setContent {
            val permissionsUiState by permissionsViewModel.uiState.collectAsState()

            AppTheme {
                    AppScreen(
                        permissionsUiState = permissionsUiState,
                        onPermissionsRequestClicked = this::requestPermissions
                    )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        permissionsViewModel.checkPermissions()
    }

    private fun requestPermissions() {
        if(!permissionsViewModel.uiState.value.isNotificationAllowed) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                openSystemAppSettings()
            } else {
                requestPostNotificationsPermissions()
            }
        } else if (!permissionsViewModel.uiState.value.isSMSReceiveAllowed) {
            requestReceiveSMSPermissions()
        }
    }

    fun openSystemAppSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // настройки уведомлений приложения
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        } else {
            // общие настройки приложения
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData("package:${packageName}".toUri())
        }
        startActivity(intent)
    }

    fun requestReceiveSMSPermissions() {
        permissionRequestLauncher.launch(Manifest.permission.RECEIVE_SMS)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPostNotificationsPermissions() {
        permissionRequestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}