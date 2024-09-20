package com.example.emailtask

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.Logger
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import arrow.core.tail
import com.example.emailtask.model.Event
import com.example.emailtask.model.RecurrenceType
import com.example.emailtask.model.Schedule
import com.example.emailtask.model.Status
import com.example.emailtask.repository.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.lang.String.format

class EventGenerator(
    context: Context,
    params: WorkerParameters,
    private val scheduleRepository: ScheduleRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val currentMoment: Instant = Clock.System.now()
            val now: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())

            scheduleRepository.getAllSchedulesWithoutFlow().filter {
                it.receivers.isNotEmpty()
                        && it.events.none { event -> event.status == Status.PENDING }
            }.mapNotNull {
                val receiver = it.receivers.first()
                val nextEvent: Event? = when (it.recurrence) {
                    RecurrenceType.NOT_REPEAT -> {
                        if (it.events.isEmpty()) {
                            Event(
                                System.currentTimeMillis(),
                                it.id,
                                receiver.id,
                                receiver.name,
                                receiver.email,
                                receiver.mobile,
                                it.message,
                                it.sentTime,
                                Status.PENDING
                            )
                        } else null
                    }

                    RecurrenceType.DAILY -> {
                        Event(
                            System.currentTimeMillis(),
                            it.id,
                            receiver.id,
                            receiver.name,
                            receiver.email,
                            receiver.mobile,
                            it.message,
                            if (it.sentTime > now) it.sentTime else LocalDateTime(
                                now.date.plus(
                                    DatePeriod(0, 0, 1)
                                ), it.sentTime.time
                            ),
                            Status.PENDING
                        )
                    }

                    RecurrenceType.WEEKLY -> {
                        val offset =
                            it.sentTime.dayOfWeek.ordinal - now.date.dayOfWeek.ordinal
                        Event(
                            System.currentTimeMillis(),
                            it.id,
                            receiver.id,
                            receiver.name,
                            receiver.email,
                            receiver.mobile,
                            it.message,
                            if (it.sentTime > now) it.sentTime else LocalDateTime(
                                now.date.plus(DatePeriod(0, 0, 7 + offset)),
                                it.sentTime.time
                            ),
                            Status.PENDING
                        )
                    }

                    RecurrenceType.MONTHLY -> {
                        Event(
                            System.currentTimeMillis(),
                            it.id,
                            receiver.id,
                            receiver.name,
                            receiver.email,
                            receiver.mobile,
                            it.message,
                            if (it.sentTime > now) it.sentTime else LocalDateTime(
                                now.date.plus(DatePeriod(0, 1, 0)), it.sentTime.time
                            ),
                            Status.PENDING
                        )
                    }

                    RecurrenceType.Annually -> {
                        Event(
                            System.currentTimeMillis(),
                            it.id,
                            receiver.id,
                            receiver.name,
                            receiver.email,
                            receiver.mobile,
                            it.message,
                            if (it.sentTime > now) it.sentTime else LocalDateTime(
                                now.date.plus(DatePeriod(1, 0, 0)), it.sentTime.time
                            ),
                            Status.PENDING
                        )
                    }

                    RecurrenceType.WEEKDAY -> {

                        val nextDate = when (now.date.dayOfWeek.ordinal) {
                            4 -> {
                                now.date.plus(DatePeriod(0, 0, 3))
                            }

                            5 -> {
                                now.date.plus(DatePeriod(0, 0, 2))
                            }

                            else -> {
                                now.date.plus(DatePeriod(0, 0, 1))
                            }
                        }

                        Event(
                            System.currentTimeMillis(),
                            it.id,
                            receiver.id,
                            receiver.name,
                            receiver.email,
                            receiver.mobile,
                            it.message,
                            if (it.sentTime > now) it.sentTime else LocalDateTime(
                                nextDate, it.sentTime.time
                            ),
                            Status.PENDING
                        )

                    }
                }

                nextEvent?.let { event ->
                    val shuffledReceivers = it.receivers.tail() + receiver
                    it.copy(
                        receivers = shuffledReceivers, events = it.events + event
                    )
                }
            }.forEach {
                scheduleRepository.insertSchedule(it)
            }

            return@withContext Result.success()
        }
    }
}
