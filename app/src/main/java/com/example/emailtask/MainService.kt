package com.example.emailtask

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.emailtask.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.TimeUnit


class MainService : Service() {

    private val channelID = "email task channel"
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i(
            "Main Service",
            "onStartCommand"
        )

        val channel = NotificationChannel(
            channelID,
            "Email Task Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Email Task channel for foreground service notification"

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("Email Task")
            .setContentText("Email Task is running")
            .build()
        startForeground(1, notification)
        return START_REDELIVER_INTENT
    }

    override fun onCreate() {
        super.onCreate()

        val appApplication = application as AppApplication

        CoroutineScope(Dispatchers.IO).launch {
            appApplication.scheduleRepository.allSchedules.collect { schedules ->
                val currentMoment: Instant = Clock.System.now()
                val now: LocalDateTime =
                    currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
                schedules.flatMap { it.events }
                    .filter { it.status == Status.PENDING }
                    .sortedBy { it.sentTime }.map { it.sentTime }.firstOrNull()?.let {
                        val delay: Long =
                            if (it <= now) 0 else
                                it.toInstant(TimeZone.currentSystemDefault()).epochSeconds - now.toInstant(
                                    TimeZone.currentSystemDefault()
                                ).epochSeconds
                        Log.i(
                            "Main Service",
                            "Trigger event sender, the delay is $delay"
                        )
                        val eventSenderRequest =
                            OneTimeWorkRequestBuilder<EventSender>().setInitialDelay(
                                delay,
                                TimeUnit.SECONDS
                            ).build()

                        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                            "event-sender", ExistingWorkPolicy.REPLACE, eventSenderRequest
                        )
                    }

                Log.i(
                    "Main Service",
                    "trigger event generator"
                )

                val eventGeneratorRequest =
                    OneTimeWorkRequestBuilder<EventGenerator>().build()

                WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                    "event-generator", ExistingWorkPolicy.REPLACE, eventGeneratorRequest
                )
            }
        }

    }
}
