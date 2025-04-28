package com.example.smstomail.data.entity

data class SettingsData(
    val login: String = "",
    val password: String = "",
    val host: String = "",
    val sslPort: Int? = null,
    val isReceiverEnabled: Boolean = false
) {
    enum class Keys {
        LOGIN,
        PASSWORD,
        SMTP_SERVER_HOST,
        SSL_PORT,
        IS_RECEIVER_ENABLED;
    }
}