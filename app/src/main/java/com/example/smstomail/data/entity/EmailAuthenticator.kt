package com.example.smstomail.data.entity

import javax.mail.Authenticator
import javax.mail.PasswordAuthentication

class EmailAuthenticator(val login: String, private val password: String): Authenticator() {
    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(login, password)
    }
}