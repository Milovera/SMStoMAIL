package com.example.smstomail.domain.service

import com.example.smstomail.data.entity.EmailAuthenticator
import com.example.smstomail.data.entity.Message
import com.example.smstomail.data.entity.Recipient
import com.example.smstomail.data.entity.SettingsData
import java.util.Properties
import javax.inject.Inject
import javax.mail.Message.RecipientType
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.net.ssl.SSLSocketFactory

class MailSender @Inject constructor(): IMessageSender {
    companion object {
        const val HOST_PROP_NAME = "mail.smtp.host"
        const val PORT_PROP_NAME = "mail.smtp.port"
        const val AUTH_PROP_NAME = "mail.smtp.auth"
        const val SSL_PROP_NAME = "mail.smtp.ssl.enable"
        const val SOCKET_FACTORY_PROP_NAME = "mail.smtp.socketFactory.class"
        const val MESSAGE_CONTENT_TYPE = "text/plain; charset=utf-8"
    }

    @Throws(MessagingException::class)
    override fun send(
        message: Message,
        recipient: Recipient,
        settings: SettingsData
    ) {
        val props = Properties()
            .apply {
                put(HOST_PROP_NAME, settings.host)
                put(PORT_PROP_NAME, settings.sslPort)
                put(AUTH_PROP_NAME, true)
                put(SSL_PROP_NAME, true)
                put(SOCKET_FACTORY_PROP_NAME, SSLSocketFactory::class.simpleName)
            }

        val session = Session.getInstance(props, EmailAuthenticator(settings.login, settings.password))

        Transport.send(
            MimeMessage(session)
                .apply {
                    setFrom(InternetAddress(settings.login))
                    setRecipient(RecipientType.TO, InternetAddress(recipient.value))
                    subject = message.sender
                    setContent(
                        MimeMultipart()
                            .apply {
                                addBodyPart(
                                    MimeBodyPart()
                                        .apply {
                                            setContent(message.body, MESSAGE_CONTENT_TYPE)
                                        }
                                )
                            }
                    )
                }
        )
    }
}