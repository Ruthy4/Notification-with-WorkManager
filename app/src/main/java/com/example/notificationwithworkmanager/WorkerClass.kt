package com.example.notificationwithworkmanager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*

class WorkerClass(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val context = context

    override fun doWork(): Result {
        setUpNotification(context)
        return Result.success()
    }

    companion object {
        fun setUpNotification(context: Context) {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, MainActivity2::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)


            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("New Word")
                .setContentText("New Word Added")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("New Word Added")
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define

                notify(notificationId, notificationBuilder.build())
            }
        }
    }
    }