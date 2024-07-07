package com.example.emailtask.model

import androidx.lifecycle.ViewModel
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
        startEventLoop()
    }

    //private val _contacts: MutableStateFlow<List<Contact>> = MutableStateFlow(
    //    listOf(
    //        Contact(1, "Tod1", "1234567890", "john@example.com"),
    //        Contact(2, "Tod2", "1234567890", "john@example.com"),
    //        Contact(3, "Tod3", "1234567890", "john@example.com"),
    //        Contact(4, "Tod4", "1234567890", "john@example.com"),
    //        Contact(5, "Tod5", "1234567890", "john@example.com"),
    //    )
    //)
    //private val _schedules: MutableStateFlow<List<Schedule>> = MutableStateFlow(
    //    listOf(
    //        Schedule(
    //            1,
    //            "test",
    //            listOf(1),
    //            listOf(),
    //            listOf(),
    //            LocalDateTime(2024, 7, 1, 11, 20, 0),
    //            RecurrenceType.NOT_REPEAT,
    //            "This is a test"
    //        ), Schedule(
    //            2,
    //            "pick up child",
    //            listOf(2, 3),
    //            listOf(),
    //            listOf(),
    //            LocalDateTime(2024, 7, 2, 11, 40, 0),
    //            RecurrenceType.WEEKLY,
    //            "This is pick up child"
    //        )
    //    )
    //)

    private val _editingContact: MutableStateFlow<Contact?> = MutableStateFlow(null)
    private val _editingSchedule: MutableStateFlow<Schedule?> = MutableStateFlow(null)

    val editingContact: StateFlow<Contact?> = _editingContact.asStateFlow()
    val editingSchedule: StateFlow<Schedule?> = _editingSchedule.asStateFlow()

    val contacts: Flow<List<Contact>> = contactRepository.allContacts
    val schedules: Flow<List<Schedule>> = scheduleRepository.allSchedules

    fun setEditingContact(contact: Contact?) {
        _editingContact.update { _ -> contact }
    }

    fun setEditingSchedule(schedule: Schedule?) {
        _editingSchedule.update { _ -> schedule }
    }

    val schedulesAndContacts: StateFlow<Pair<List<Schedule>, List<Contact>>> =
        combine(schedules, contacts) { s, c -> Pair(s, c) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Pair(listOf(), listOf())
        )

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            contactRepository.insert(contact)
        }
        //_contacts.update { existingContacts ->
        //    val isNewContact = existingContacts.find { it.id == contact.id } == null
        //    if (isNewContact) {
        //        contacts.value.plus(contact)
        //    } else {
        //        existingContacts.map { if (it.id == contact.id) contact else it }
        //    }
        //}
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.insertSchedule(schedule)
        }

        //_schedules.update { existingSchedules ->
        //    val isNewSchedule = existingSchedules.find { it.id == schedule.id } == null
        //    if (isNewSchedule) {
        //        existingSchedules.plus(schedule)
        //    } else {
        //        existingSchedules.map {
        //            if (it.id == schedule.id) schedule.copy(
        //                pendingEvents = listOf()
        //            ) else it
        //        }
        //    }
        //}
    }


    // fun moveScheduleMember(from: Int, to: Int) {
    //     _editingSchedule.value?.let {
    //         val mutableMembers = it.receivers.toMutableList()
    //         val member = mutableMembers.removeAt(from)
    //         mutableMembers.add(if (to > from) to - 1 else to, member)
    //         _editingSchedule.value = it.copy(receivers = mutableMembers.toList())
    //     }
    // }


    private fun startEventLoop() {
        viewModelScope.launch {
            while (isActive) {
                generateEvents()
                delay(1 * 60 * 1000)
            }
        }
    }

    private suspend fun generateEvents() {
        withContext(Dispatchers.Default) {
            val currentMoment: Instant = Clock.System.now()
            val now: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())

            //TODO consider update pending message dynamically, which mean regenerate it according to the latest schedule
            _schedules.update { existingSchedules ->
                existingSchedules.map {
                    when (it.recurrence) {
                        RecurrenceType.NOT_REPEAT -> {
                            if (it.sentEvents.isEmpty() && it.pendingEvents.isEmpty() && it.sentTime > now) {
                                val event = Event(
                                    System.currentTimeMillis(),
                                    it.receivers.first(),
                                    it.message,
                                    it.sentTime
                                )
                                it.copy(pendingEvents = listOf(event))
                            } else {
                                it
                            }
                        }

                        RecurrenceType.DAILY -> {
                            if (it.pendingEvents.none { event -> event.sentTime >= now }) {
                                val event = Event(
                                    System.currentTimeMillis(),
                                    it.receivers.first(),
                                    it.message,
                                    if (it.sentTime > now) it.sentTime else LocalDateTime(
                                        now.date.plus(
                                            DatePeriod(0, 0, 1)
                                        ), it.sentTime.time
                                    )
                                )
                                it.copy(pendingEvents = listOf(event))
                            } else {
                                it
                            }
                        }

                        RecurrenceType.WEEKLY -> {
                            if (it.pendingEvents.none { event -> event.sentTime >= now }) {
                                val offset =
                                    it.sentTime.dayOfWeek.ordinal - now.date.dayOfWeek.ordinal
                                val event = Event(
                                    System.currentTimeMillis(),
                                    it.receivers.first(),
                                    it.message,
                                    if (it.sentTime > now) it.sentTime else LocalDateTime(
                                        now.date.plus(DatePeriod(0, 0, 7 + offset)),
                                        it.sentTime.time
                                    )
                                )
                                it.copy(pendingEvents = listOf(event))
                            } else {
                                it
                            }
                        }

                        RecurrenceType.MONTHLY -> {
                            if (it.pendingEvents.none { event -> event.sentTime >= now }) {
                                val event = Event(
                                    System.currentTimeMillis(),
                                    it.receivers.first(),
                                    it.message,
                                    if (it.sentTime > now) it.sentTime else LocalDateTime(
                                        now.date.plus(DatePeriod(0, 1, 0)), it.sentTime.time
                                    )
                                )
                                it.copy(pendingEvents = listOf(event))
                            } else {
                                it
                            }
                        }

                        RecurrenceType.Annually -> {
                            if (it.pendingEvents.none { event -> event.sentTime >= now }) {
                                val event = Event(
                                    System.currentTimeMillis(),
                                    it.receivers.first(),
                                    it.message,
                                    if (it.sentTime > now) it.sentTime else LocalDateTime(
                                        now.date.plus(DatePeriod(1, 0, 0)), it.sentTime.time
                                    )
                                )
                                it.copy(pendingEvents = listOf(event))
                            } else {
                                it
                            }
                        }

                        RecurrenceType.WEEKDAY -> {
                            if (it.pendingEvents.none { event -> event.sentTime >= now }) {

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
                                    it.receivers.first(),
                                    it.message,
                                    if (it.sentTime > now) it.sentTime else LocalDateTime(
                                        nextDate, it.sentTime.time
                                    )
                                )
                                it.copy(pendingEvents = listOf(event))
                            } else {
                                it
                            }
                        }
                    }
                }
            }
        }

    }

}