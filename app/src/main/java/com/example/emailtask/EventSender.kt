package com.example.emailtask

import android.content.Context
import android.telephony.SmsManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.emailtask.model.Status
import com.example.emailtask.repository.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class EventSender(
    private val context: Context,
    params: WorkerParameters,
    private val scheduleRepository: ScheduleRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val currentMoment: Instant = Clock.System.now()
            val now: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
            val smsManager = SmsManager.getDefault()

            scheduleRepository.allSchedules.collect { schedules ->

                schedules.forEach { schedule ->
                    val pendingEvents =
                        schedule.events.filter { it.status == Status.PENDING && it.sentTime <= now }
                            .sortedBy { it.sentTime }

                    val processedEvents = pendingEvents.map { event ->
                        try {
                            smsManager.sendTextMessage(
                                event.receiverMobile,
                                null,
                                event.message,
                                null,
                                null
                            )
                            event.copy(status = Status.SUCCESS)
                        } catch (e: Exception) {
                            event.copy(status = Status.FAILURE)
                        }
                    }

                    scheduleRepository.insertEvent(*processedEvents.toTypedArray())
                }

            }
            return@withContext Result.success()
        }
    }
}