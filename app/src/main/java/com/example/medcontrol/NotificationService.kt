package com.example.medcontrol

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationService(
    private val context: Context,
) {

    private val notificationManager = context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(title: String, text: String, id: Int? = null) {
        // Create an intent to launch the main activity when the notification is clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(text)
            .setContentTitle(title)
            .setContentIntent(pendingIntent)  // Set the pending intent here
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)  // Automatically remove the notification when clicked
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
