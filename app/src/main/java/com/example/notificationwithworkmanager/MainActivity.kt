package com.example.notificationwithworkmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.notificationwithworkmanager.WorkerClass.Companion.setUpNotification
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
        createWorkRequest()

        findViewById<TextView>(R.id.NotifyMeButton).setOnClickListener {
            showNotification()
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createWorkRequest() {
        //set charging constraints
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        //periodic work request
        val workRequest = PeriodicWorkRequest.Builder(WorkerClass::class.java, 5, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(workRequest)

        //observe result state
        WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(workRequest.id)
            .observe(this) { workInfo ->
                findViewById<TextView>(R.id.notificationResult).append(workInfo.state.name + "\n")
            }
    }

    private fun showNotification() {
        setUpNotification(this)
    }

    private fun cancelWorkRequest() {
        WorkManager.getInstance(applicationContext).cancelAllWork()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        cancelWorkRequest()
    }
}