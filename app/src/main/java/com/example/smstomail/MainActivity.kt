package com.example.smstomail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.view.View

class MainActivity : AppCompatActivity() {
    companion object {
        const val LOG_TAG = "MainActivity"
        const val PERMISSION_REQUEST_CODE = 1245
    }

    private lateinit var permissionTextView: TextView
    private lateinit var permissionButton: Button
    private lateinit var receiverStatusTextView: TextView
    private lateinit var receiverSwitchButton: Button
    private lateinit var settingsButton: Button
    private lateinit var settingsLayout: LinearLayout
    private lateinit var loginEditText: EditText
    private lateinit var passwEditText: EditText
    private lateinit var smtpServerEditText: EditText
    private lateinit var smtpSSLEditText: EditText
    private lateinit var recipientsEditText: EditText
    private lateinit var applySettingsButton: Button
    private lateinit var testButton: Button

    private var permissionsIsGained = false
    private var isReceiverEnabled = false

    val isSettingsFieldsFilled: Boolean
        get() {
            return loginEditText.text.isNotEmpty() && passwEditText.text.isNotEmpty() && recipientsEditText.text.isNotEmpty() &&
                    smtpServerEditText.text.isNotEmpty() && smtpSSLEditText.text.isNotEmpty()
        }

    private val settingsEditorChecker: TextWatcher =
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    isSettingsFieldsFilled.let { value ->
                        applySettingsButton.isEnabled = value
                        testButton.isEnabled = value
                    }
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionTextView = findViewById(R.id.permissions_text)
        permissionButton = findViewById(R.id.permissions_button)
        receiverStatusTextView = findViewById(R.id.receiver_status_text)
        receiverSwitchButton = findViewById(R.id.receiver_switch_button)
        settingsButton = findViewById(R.id.settings_button)
        settingsLayout = findViewById(R.id.settings_layout)
        loginEditText = findViewById(R.id.login_edit)
        passwEditText = findViewById(R.id.passw_edit)
        smtpServerEditText = findViewById(R.id.smtp_server_edit)
        smtpSSLEditText = findViewById(R.id.smtp_port_edit)
        recipientsEditText = findViewById(R.id.recipients_edit)
        applySettingsButton = findViewById(R.id.apply_settings_button)
        testButton = findViewById(R.id.test_settings_button)

        loginEditText.addTextChangedListener(settingsEditorChecker)
        passwEditText.addTextChangedListener(settingsEditorChecker)
        recipientsEditText.addTextChangedListener(settingsEditorChecker)
        smtpServerEditText.addTextChangedListener(settingsEditorChecker)
        smtpSSLEditText.addTextChangedListener(settingsEditorChecker)

        permissionButton.setOnClickListener { requestPermissions(arrayOf(Manifest.permission.RECEIVE_SMS), PERMISSION_REQUEST_CODE) }
        receiverSwitchButton.setOnClickListener { switchReceiverStatus() }
        applySettingsButton.setOnClickListener { saveSettings() }
        settingsButton.setOnClickListener { showSettings() }
        testButton.setOnClickListener { testSettings() }

        loadSavedPreferences()
    }

    override fun onStart() {
        super.onStart()
        checkPermissionsAndShowStatus()
        checkReceiverStatusAndShowStatus()
    }

    private fun checkPermissionsAndShowStatus() {
        permissionsIsGained = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
        if(permissionsIsGained) {
            permissionButton.visibility = View.GONE
            permissionTextView.visibility = View.GONE
        } else {
            permissionTextView.visibility = View.VISIBLE
            permissionButton.visibility = View.VISIBLE
        }
    }

    private fun checkReceiverStatusAndShowStatus() {
        if(permissionsIsGained) {
            receiverStatusTextView.visibility = View.VISIBLE
            receiverSwitchButton.visibility = View.VISIBLE

            try {
                val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
                isReceiverEnabled = sharedPreferences.getBoolean(PreferencesKeys.RECEIVER_ENABLED, false)
            } catch (ex: Resources.NotFoundException) {}

            if(isReceiverEnabled) {
                receiverStatusTextView.setText(R.string.receiver_enabled_text)
                receiverSwitchButton.setText(R.string.disable_text)
            } else {
                receiverStatusTextView.setText(R.string.receiver_disabled_text)
                receiverSwitchButton.setText(R.string.enable_text)
            }
        } else {
            receiverStatusTextView.visibility = View.GONE
            receiverSwitchButton.visibility = View.GONE
        }

    }

    private fun loadSavedPreferences() {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        try {
            loginEditText.setText(sharedPreferences.getString(PreferencesKeys.LOGIN_KEY, ""))
            passwEditText.setText(sharedPreferences.getString(PreferencesKeys.PASSW_KEY, "")!!)
            smtpServerEditText.setText(sharedPreferences.getString(PreferencesKeys.SERVER_KEY, "")!!)
            smtpSSLEditText.setText(sharedPreferences.getString(PreferencesKeys.SSL_PORT, "0"))
            recipientsEditText.setText(sharedPreferences.getString(PreferencesKeys.RECIPIENTS, "")!!)
        } catch (ex: Resources.NotFoundException) {
            return
        }
    }

    private fun showSettings() {
        settingsButton.text = getText(R.string.settings_hide_button_text)
        settingsLayout.visibility = View.VISIBLE
        isSettingsFieldsFilled.let { value ->
            applySettingsButton.isEnabled = value
            testButton.isEnabled = value
        }

        settingsButton.setOnClickListener { hideSettings() }
    }

    private fun hideSettings() {
        settingsButton.text = getText(R.string.settings_show_button_text)
        settingsLayout.visibility = View.GONE
        settingsButton.setOnClickListener { showSettings() }
    }

    private fun saveSettings() {
        with(getPreferences(Context.MODE_PRIVATE).edit()) {
            putString(PreferencesKeys.LOGIN_KEY, loginEditText.text.toString())
            putString(PreferencesKeys.PASSW_KEY, passwEditText.text.toString())
            putString(PreferencesKeys.SERVER_KEY, smtpServerEditText.text.toString())
            putString(PreferencesKeys.SSL_PORT, smtpSSLEditText.text.toString())
            putString(PreferencesKeys.RECIPIENTS, recipientsEditText.text.toString())
            apply()
        }
    }

    private fun switchReceiverStatus() {
        isReceiverEnabled = !isReceiverEnabled
        with(getPreferences(Context.MODE_PRIVATE).edit()) {
            putBoolean(PreferencesKeys.RECEIVER_ENABLED, isReceiverEnabled)
            apply()
        }
        checkReceiverStatusAndShowStatus()
    }

    private fun testSettings() {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        checkPermissionsAndShowStatus()
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}