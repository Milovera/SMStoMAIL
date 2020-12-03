package com.example.smstomail

import android.util.Log
import java.util.*
import javax.mail.*
import javax.mail.internet.*
import kotlin.math.log

class EmailAuthenticator(val login: String, val passw: String): Authenticator() {
    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(login, passw)
    }
}

class SMTPMailSender(host: String, port: String, private val auth: EmailAuthenticator) {
    companion object {
        const val LOG_TAG = "SMTPMailSender"
    }

    private val prop = Properties()
    private var session: Session

    init {
        prop["mail.smtp.host"] = host
        prop["mail.smtp.port"] = port
        prop["mail.smtp.auth"] = "true"
        prop["mail.smtp.ssl.enable"] = "true"
        prop["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"

        session = Session.getInstance(prop, auth)
        session.debug = false
    }

    @Throws(AddressException::class, MessagingException::class)
    fun doSend(emailTo: String, emailTitle: String, emailBody: String) {
            Transport.send(
                MimeMessage(session).apply {
                    setFrom(InternetAddress(auth.login))
                    setRecipient(Message.RecipientType.TO, InternetAddress(emailTo))
                    subject = emailTitle
                    setContent(
                        MimeMultipart().apply {
                            addBodyPart(
                                MimeBodyPart().apply {
                                    setContent(emailBody, "text/plain; charset=utf-8")
                                })
                        })
                })
    }
}