package com.example.emailtask.repository

import androidx.annotation.WorkerThread
import com.example.emailtask.data.EventDao
import com.example.emailtask.data.ScheduleDao
import com.example.emailtask.data.ScheduleWithReceiversAndEvents
import com.example.emailtask.model.Event
import com.example.emailtask.model.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScheduleRepository(private val scheduleDao: ScheduleDao, private val eventDao: EventDao) {

    val allSchedules: Flow<List<Schedule>> =
        scheduleDao.getAll().map { scheduleEntities -> scheduleEntities.map { it.toSchedule() } }

    @WorkerThread
    suspend fun insertSchedule(schedule: Schedule) {
        scheduleDao.insertSchedules(ScheduleWithReceiversAndEvents.fromSchedule(schedule))
    }

    @WorkerThread
    suspend fun insertEvent(schedule: Schedule, event: Event) {
        eventDao.insertEvents(ScheduleWithReceiversAndEvents.toEventEntity(schedule, event))
    }
}