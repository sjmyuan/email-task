package com.example.emailtask.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.emailtask.data.ContactDao
import com.example.emailtask.repository.ContactRepository
import com.example.emailtask.repository.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class App1ViewModel(
    private val contactRepository: ContactRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    init {
        //startEventLoop()
    }

    private val _editingContact: MutableStateFlow<Contact?> = MutableStateFlow(null)
    private val _editingSchedule: MutableStateFlow<Schedule?> = MutableStateFlow(null)

    val editingContact: StateFlow<Contact?> = _editingContact.asStateFlow()
    val editingSchedule: StateFlow<Schedule?> = _editingSchedule.asStateFlow()

    val contacts: StateFlow<List<Contact>> =
        contactRepository.allContacts.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())
    val schedules: StateFlow<List<Schedule>> =
        scheduleRepository.allSchedules.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    fun setEditingContact(contact: Contact?) {
        _editingContact.update { _ -> contact }
    }

    fun setEditingSchedule(schedule: Schedule?) {
        _editingSchedule.update { _ -> schedule }
    }

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            contactRepository.insert(contact)
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.insertSchedule(schedule)
        }
    }


    // fun moveScheduleMember(from: Int, to: Int) {
    //     _editingSchedule.value?.let {
    //         val mutableMembers = it.receivers.toMutableList()
    //         val member = mutableMembers.removeAt(from)
    //         mutableMembers.add(if (to > from) to - 1 else to, member)
    //         _editingSchedule.value = it.copy(receivers = mutableMembers.toList())
    //     }
    // }


    //private fun startEventLoop() {
    //    viewModelScope.launch {
    //        while (isActive) {
    //            generateEvents()
    //            delay(1 * 60 * 1000)
    //        }
    //    }
    //}

    //private suspend fun generateEvents() {
    //    withContext(Dispatchers.Default) {
    //        val currentMoment: Instant = Clock.System.now()
    //        val now: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())

    //        //TODO consider update pending message dynamically, which mean regenerate it according to the latest schedule
    //        _schedules.update { existingSchedules ->
    //            existingSchedules.map {
    //                when (it.recurrence) {
    //                    RecurrenceType.NOT_REPEAT -> {
    //                        if (it.sentEvents.isEmpty() && it.pendingEvents.isEmpty() && it.sentTime > now) {
    //                            val event = Event(
    //                                System.currentTimeMillis(),
    //                                it.receivers.first(),
    //                                it.message,
    //                                it.sentTime
    //                            )
    //                            it.copy(pendingEvents = listOf(event))
    //                        } else {
    //                            it
    //                        }
    //                    }

    //                    RecurrenceType.DAILY -> {
    //                        if (it.pendingEvents.none { event -> event.sentTime >= now }) {
    //                            val event = Event(
    //                                System.currentTimeMillis(),
    //                                it.receivers.first(),
    //                                it.message,
    //                                if (it.sentTime > now) it.sentTime else LocalDateTime(
    //                                    now.date.plus(
    //                                        DatePeriod(0, 0, 1)
    //                                    ), it.sentTime.time
    //                                )
    //                            )
    //                            it.copy(pendingEvents = listOf(event))
    //                        } else {
    //                            it
    //                        }
    //                    }

    //                    RecurrenceType.WEEKLY -> {
    //                        if (it.pendingEvents.none { event -> event.sentTime >= now }) {
    //                            val offset =
    //                                it.sentTime.dayOfWeek.ordinal - now.date.dayOfWeek.ordinal
    //                            val event = Event(
    //                                System.currentTimeMillis(),
    //                                it.receivers.first(),
    //                                it.message,
    //                                if (it.sentTime > now) it.sentTime else LocalDateTime(
    //                                    now.date.plus(DatePeriod(0, 0, 7 + offset)),
    //                                    it.sentTime.time
    //                                )
    //                            )
    //                            it.copy(pendingEvents = listOf(event))
    //                        } else {
    //                            it
    //                        }
    //                    }

    //                    RecurrenceType.MONTHLY -> {
    //                        if (it.pendingEvents.none { event -> event.sentTime >= now }) {
    //                            val event = Event(
    //                                System.currentTimeMillis(),
    //                                it.receivers.first(),
    //                                it.message,
    //                                if (it.sentTime > now) it.sentTime else LocalDateTime(
    //                                    now.date.plus(DatePeriod(0, 1, 0)), it.sentTime.time
    //                                )
    //                            )
    //                            it.copy(pendingEvents = listOf(event))
    //                        } else {
    //                            it
    //                        }
    //                    }

    //                    RecurrenceType.Annually -> {
    //                        if (it.pendingEvents.none { event -> event.sentTime >= now }) {
    //                            val event = Event(
    //                                System.currentTimeMillis(),
    //                                it.receivers.first(),
    //                                it.message,
    //                                if (it.sentTime > now) it.sentTime else LocalDateTime(
    //                                    now.date.plus(DatePeriod(1, 0, 0)), it.sentTime.time
    //                                )
    //                            )
    //                            it.copy(pendingEvents = listOf(event))
    //                        } else {
    //                            it
    //                        }
    //                    }

    //                    RecurrenceType.WEEKDAY -> {
    //                        if (it.pendingEvents.none { event -> event.sentTime >= now }) {

    //                            val nextDate = when (now.date.dayOfWeek.ordinal) {
    //                                4 -> {
    //                                    now.date.plus(DatePeriod(0, 0, 3))
    //                                }

    //                                5 -> {
    //                                    now.date.plus(DatePeriod(0, 0, 2))
    //                                }

    //                                else -> {
    //                                    now.date.plus(DatePeriod(0, 0, 1))
    //                                }
    //                            }

    //                            val event = Event(
    //                                System.currentTimeMillis(),
    //                                it.receivers.first(),
    //                                it.message,
    //                                if (it.sentTime > now) it.sentTime else LocalDateTime(
    //                                    nextDate, it.sentTime.time
    //                                )
    //                            )
    //                            it.copy(pendingEvents = listOf(event))
    //                        } else {
    //                            it
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //    }

    //}

}

class App1ViewModelFactory(
    private val contactRepository: ContactRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(App1ViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return App1ViewModel(contactRepository, scheduleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}