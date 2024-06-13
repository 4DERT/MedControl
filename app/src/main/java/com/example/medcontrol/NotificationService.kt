package com.example.medcontrol

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class NotificationService(
    private val context: Context
) {

    private val notificationManager = context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(title: String, text: String, id: Int? = null) {
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(text)
            .setContentTitle(title)
            .setOnlyAlertOnce(true)
            .build()

        notificationManager.notify(
            id ?: notification_id++,
            notification
        )
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "kanal_zero"
        var notification_id = 1
    }

}