package com.example.smoothieshopapp.model

/**
 *
 */
data class NotificationPush(
    // Notification data (title, message)
    val data: NotificationData,
    // It is token or topic
    val to: String
)
