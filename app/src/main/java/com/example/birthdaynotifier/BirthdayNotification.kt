package com.example.birthdaynotifier

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class BirthdayNotification {
    companion object Functions {
        private const val channelId = "birthdayChannel"

        fun sendBirthdayNotification(context: Context, event: Event) {
            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_round_cake_24).setContentTitle(event.title)
                .setContentText(event.description)
                .setStyle(NotificationCompat.BigTextStyle().bigText(event.description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            NotificationManagerCompat.from(context).notify(kotlin.random.Random.nextInt(), builder.build())
        }

        fun createBirthdayNotificationChannel(context: Context) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}