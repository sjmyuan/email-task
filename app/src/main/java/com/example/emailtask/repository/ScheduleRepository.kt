package com.example.emailtask.repository

import androidx.annotation.WorkerThread
import androidx.room.Transaction
import com.example.emailtask.data.ContactEntity
import com.example.emailtask.data.EventDao
import com.example.emailtask.data.EventEntity
import com.example.emailtask.data.ScheduleContactCrossRef
import com.example.emailtask.data.ScheduleDao
import com.example.emailtask.data.ScheduleEntity
import com.example.emailtask.data.ScheduleWithReceiversAndEvents
import com.example.emailtask.model.Contact
import com.example.emailtask.model.Event
import com.example.emailtask.model.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScheduleRepository(private val scheduleDao: ScheduleDao, private val eventDao: EventDao) {

    val allSchedules: Flow<List<Schedule>> =
        scheduleDao.getAll().map { scheduleEntities -> scheduleEntities.map { it.toSchedule() } }

    suspend fun getAllSchedulesWithoutFlow(): List<Schedule> =
        scheduleDao.getAllWithoutFlow().map { it.toSchedule() }

    @WorkerThread
    @Transaction
    suspend fun insertSchedule(schedule: Schedule) {
        val entity = ScheduleWithReceiversAndEvents.fromSchedule1(schedule)
        scheduleDao.insertSchedules(entity.schedule)
        scheduleDao.deleteScheduleReceivers(scheduleId = entity.schedule.scheduleId)
        scheduleDao.insertScheduleReceivers(*entity.receivers.mapIndexed { index, it ->
            ScheduleContactCrossRef(
                entity.schedule.scheduleId, it.contactId, index
            )
        }.toTypedArray())
        eventDao.deleteEventsByScheduleId(entity.schedule.scheduleId)
        eventDao.insertEvents(*entity.events.toTypedArray())
    }

    @WorkerThread
    @Transaction
    suspend fun deleteSchedule(schedule: Schedule) {
        val entity = ScheduleWithReceiversAndEvents.fromSchedule1(schedule)
        scheduleDao.deleteScheduleReceivers(schedule.id)
        scheduleDao.deleteSchedules(entity.schedule)
    }

    @WorkerThread
    suspend fun insertEvent(vararg events: Event) {
        eventDao.insertEvents(*events.map { EventEntity.fromEvent(it) }.toTypedArray())
    }

    @WorkerThread
    suspend fun deleteContact(contact: Contact) {
        scheduleDao.deleteReceiverSchedules(contact.id)
    }

}
