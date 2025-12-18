package com.example.labtest03.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.labtest03.MainActivity
import com.example.labtest03.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel if needed (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "hydration_channel",
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notifications to remind you to drink water"
            nm.createNotificationChannel(channel)
        }

        val i = Intent(context, MainActivity::class.java)
        val p = PendingIntent.getActivity(
            context,
            0,
            i,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notif = NotificationCompat.Builder(context, "hydration_channel")
            .setContentTitle("Time to drink water")
            .setContentText("Hydration reminder from Wellness Buddy")
            .setSmallIcon(R.drawable.ic_water_drop) // create vector icon
            .setContentIntent(p)
            .setAutoCancel(true)
            .build()
        nm.notify(1001, notif)
    }
}
