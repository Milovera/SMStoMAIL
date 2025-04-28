package com.example.smstomail.service

import android.content.Context
import androidx.concurrent.futures.await
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerFactory
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.smstomail.R
import com.example.smstomail.data.entity.Message
import com.example.smstomail.data.entity.Recipient
import com.example.smstomail.data.entity.SettingsData
import com.example.smstomail.data.repository.ISettingsRepository
import com.example.smstomail.domain.service.IMessageSender
import com.example.smstomail.domain.service.INotificationSender
import com.example.smstomail.domain.workers.AppWorkerFactory
import com.example.smstomail.domain.workers.MailSenderWorker
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.mail.AuthenticationFailedException
import javax.mail.SendFailedException
import javax.mail.internet.AddressException

@RunWith(AndroidJUnit4::class)
class MailSenderWorkerTest {
    private lateinit var context: Context
    private lateinit var executor: Executor

    private val mailSender = object: IMessageSender {
        val messages = mutableListOf<Message>()
        override fun send(
            message: Message,
            recipient: Recipient,
            settings: SettingsData
        ) {
            messages += message
        }
    }
    private val notificationSender = object: INotificationSender {
        val notifications = mutableListOf<String>()
        override fun showNotification(message: String) {
            notifications += message
        }
    }
    private val settingsRepository = object: ISettingsRepository {
        override fun read() = SettingsData(
                login = "login",
                password = "password",
                host = "host",
                sslPort = 443,
                isReceiverEnabled = true
            )
        override fun write(data: SettingsData) = throw AssertionError("SettingsRepository::write")
    }
    private fun makeWorkerFactory(
        notificationSender: INotificationSender = this.notificationSender,
        settingsRepository: ISettingsRepository = this.settingsRepository,
        mailSender: IMessageSender = this.mailSender
    ): WorkerFactory {
        return AppWorkerFactory(
            notificationSender = notificationSender,
            mailSender = mailSender,
            settingsRepository = settingsRepository
        )
    }
    private fun buildWork(message: Message, recipient: String = "recipient",
                          notificationSender: INotificationSender = this.notificationSender,
                          settingsRepository: ISettingsRepository = this.settingsRepository,
                          mailSender: IMessageSender = this.mailSender): ListenableWorker {
        return TestListenableWorkerBuilder
            .from(context, MailSenderWorker.createRequest(message, recipient))
            .setWorkerFactory(
                makeWorkerFactory(
                    notificationSender,
                    settingsRepository,
                    mailSender
                )
            )
            .build()
    }

    private val validMessage = Message("sender", "body")
    private val messageWithEmptySender = Message("", "body")
    private val messageWithEmptyBody = Message("sender", "")

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        executor = Executors.newSingleThreadExecutor()
    }

    @Test
    fun sendValidMessage_SuccessfulSend() {
        val work = buildWork(validMessage)
        val result = runBlocking {
            work.startWork().await()
        }

        assertThat(result, `is`(ListenableWorker.Result.success()))
        assertTrue(notificationSender.notifications.isEmpty())
        assertTrue(mailSender.messages.isNotEmpty())
    }

    @Test
    fun sendMessageWithEmptyBody_FailSend() {
        val work = buildWork(messageWithEmptyBody)
        val result = runBlocking {
            work.startWork().await()
        }

        assertThat(result, `is`(ListenableWorker.Result.failure()))
        assertTrue(notificationSender.notifications.isEmpty())
        assertTrue(mailSender.messages.isEmpty())
    }

    @Test
    fun sendMessageWithEmptySender_FailSend() {
        val work = buildWork(messageWithEmptySender)
        val result = runBlocking {
            work.startWork().await()
        }

        assertThat(result, `is`(ListenableWorker.Result.failure()))
        assertTrue(notificationSender.notifications.isEmpty())
        assertTrue(mailSender.messages.isEmpty())
    }

    @Test
    fun sendMessageWithoutSettings_FailSend() {
        val work = buildWork(
            message = validMessage,
            settingsRepository = object: ISettingsRepository {
                override fun read() = null
                override fun write(data: SettingsData) = throw AssertionError("SettingsRepository::write()")
            }
        )

        val result = runBlocking {
            work.startWork().await()
        }

        assertThat(result, `is`(ListenableWorker.Result.failure()))
        assertTrue(notificationSender.notifications.isEmpty())
        assertTrue(mailSender.messages.isEmpty())
    }

    @Test
    fun sendMessageWithoutRecipient_FailSend() {
        val work = buildWork(message = validMessage, recipient = "")

        val result = runBlocking {
            work.startWork().await()
        }

        assertThat(result, `is`(ListenableWorker.Result.failure()))
        assertTrue(notificationSender.notifications.isEmpty())
        assertTrue(mailSender.messages.isEmpty())
    }

    @Test
    fun sendMessageWithoutWorkData_FailSend() {
        val work = TestListenableWorkerBuilder
            .from(context, OneTimeWorkRequest.Builder(MailSenderWorker::class.java).build())
            .setWorkerFactory(
                AppWorkerFactory(
                    notificationSender = notificationSender,
                    mailSender = mailSender,
                    settingsRepository = settingsRepository
                )
            )
            .build()

        val result = runBlocking {
            work.startWork().await()
        }

        assertThat(result, `is`(ListenableWorker.Result.failure()))
        assertTrue(notificationSender.notifications.isEmpty())
        assertTrue(mailSender.messages.isEmpty())
    }

    @Test
    fun invalidRecipientAddress_NotificationShow() {
        val recipient = "test"
        val sender = object: IMessageSender {
            override fun send(
                message: Message,
                recipient: Recipient,
                settings: SettingsData
            ) = throw AddressException("invalid addresses")
        }

        val work = buildWork(message = validMessage, mailSender = sender, recipient = recipient)

        val result = runBlocking {
            work.startWork().await()
        }

        assertThat(result, `is`(ListenableWorker.Result.failure()))
        assertTrue(notificationSender.notifications.isNotEmpty())
        val notificationText = notificationSender.notifications.first()
        val notificationExpected = context.getString(R.string.error_incorrect_address, recipient)

        assertEquals(notificationExpected, notificationText)
    }

    @Test
    fun authenticationFailed_NotificationShow() {
        val sender = object: IMessageSender {
            override fun send(
                message: Message,
                recipient: Recipient,
                settings: SettingsData
            ) = throw AuthenticationFailedException("authentication failed")
        }

        val work = buildWork(message = validMessage, mailSender = sender)

        val result = runBlocking {
            work.startWork().await()
        }

        assertThat(result, `is`(ListenableWorker.Result.failure()))
        assertTrue(notificationSender.notifications.isNotEmpty())
        val notificationText = notificationSender.notifications.first()
        val notificationExpected = context.getString(R.string.error_authentication)

        assertEquals(notificationExpected, notificationText)
    }

    @Test
    fun couldNotConnectToHost_NotificationShow() {
        val sender = object: IMessageSender {
            override fun send(
                message: Message,
                recipient: Recipient,
                settings: SettingsData
            ) = throw SendFailedException("couldn't connect to host")
        }

        val work = buildWork(message = validMessage, mailSender = sender)

        val result = runBlocking {
            work.startWork().await()
        }

        assertThat(result, `is`(ListenableWorker.Result.failure()))
        assertTrue(notificationSender.notifications.isNotEmpty())
        val notificationText = notificationSender.notifications.first()
        val notificationExpected = context.getString(R.string.error_connecting)

        assertEquals(notificationExpected, notificationText)
    }

    @Test
    fun unknownExceptionsDuringMailSending_NotificationShow() {
        val recipient = "test"
        val sender = object: IMessageSender {
            override fun send(
                message: Message,
                recipient: Recipient,
                settings: SettingsData
            ) = throw RuntimeException()
        }

        val work = buildWork(message = validMessage, mailSender = sender, recipient = recipient)

        val result = runBlocking {
            work.startWork().await()
        }

        assertThat(result, `is`(ListenableWorker.Result.failure()))
        assertTrue(notificationSender.notifications.isNotEmpty())
        val notificationText = notificationSender.notifications.first()
        val notificationExpected = context.getString(R.string.error_unexpected, recipient)

        assertEquals(notificationExpected, notificationText)
    }
}
