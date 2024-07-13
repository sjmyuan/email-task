package com.example.emailtask

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.emailtask.model.Event
import com.example.emailtask.model.RecurrenceType
import com.example.emailtask.model.Status
import com.example.emailtask.repository.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class EventGenerator(
    context: Context,
    params: WorkerParameters,
    private val scheduleRepository: ScheduleRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val currentMoment: Instant = Clock.System.now()
        val now: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())

        return withContext(Dispatchers.IO) {
            scheduleRepository.allSchedules.collect { schedules ->
                schedules.filter { it.receivers.isNotEmpty() }
                    .forEach {
                        val pendingEvents =
                            it.events.filter { event -> event.status == Status.PENDING }
                        val receiver = it.receivers.first()
                        when (it.recurrence) {
                            RecurrenceType.NOT_REPEAT -> {
                                if (pendingEvents.isEmpty() && it.sentTime > now) {
                                    val event = Event(
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
                                    scheduleRepository.insertEvent(event)
                                }
                            }

                            RecurrenceType.DAILY -> {
                                if (pendingEvents.none { event -> event.sentTime >= now }) {
                                    val event = Event(
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
                                    scheduleRepository.insertEvent(event)
                                }
                            }

                            RecurrenceType.WEEKLY -> {
                                if (pendingEvents.none { event -> event.sentTime >= now }) {
                                    val offset =
                                        it.sentTime.dayOfWeek.ordinal - now.date.dayOfWeek.ordinal
                                    val event = Event(
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
                                    scheduleRepository.insertEvent(event)
                                }
                            }

                            RecurrenceType.MONTHLY -> {
                                if (pendingEvents.none { event -> event.sentTime >= now }) {
                                    val event = Event(
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
                                    scheduleRepository.insertEvent(event)
                                }
                            }

                            RecurrenceType.Annually -> {
                                if (pendingEvents.none { event -> event.sentTime >= now }) {
                                    val event = Event(
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
                                    scheduleRepository.insertEvent(event)
                                }
                            }

                            RecurrenceType.WEEKDAY -> {
                                if (pendingEvents.none { event -> event.sentTime >= now }) {

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

                                    val event = Event(
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
                                    scheduleRepository.insertEvent(event)

                                }
                            }
                        }
                    }
            }
            return@withContext Result.success()
        }
    }
}