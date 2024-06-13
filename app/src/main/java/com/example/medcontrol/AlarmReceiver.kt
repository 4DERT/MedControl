package com.example.medcontrol

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("AlarmReceiver", "Alarm received")

        if(context == null || intent == null){
            return
        }

        val medicineName = intent.getStringExtra("medicine_name")
        val notificationId = intent.getIntExtra("notification_id", 0)
        val notificationService = NotificationService(context)

        val title = context.getString(R.string.time_for_medicine)
        val message = context.getString(R.string.time_for_medicine_text, medicineName)

        notificationService.showNotification(title, message, notificationId)

    }
}
