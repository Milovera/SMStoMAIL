package com.example.smstomail.domain.service

interface INotificationSender {
    fun showNotification(message: String)
}