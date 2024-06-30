package com.example.emailtask.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime

class App1ViewModel : ViewModel() {

    // init {
    //     startEventLoop()
    // }

    private val _contacts: MutableStateFlow<List<Contact>> = MutableStateFlow(
        listOf(
            Contact(1, "Tod1", "1234567890", "john@example.com"),
            Contact(2, "Tod2", "1234567890", "john@example.com"),
            Contact(3, "Tod3", "1234567890", "john@example.com"),
            Contact(4, "Tod4", "1234567890", "john@example.com"),
            Contact(5, "Tod5", "1234567890", "john@example.com"),
        )
    )
    private val _schedules: MutableStateFlow<List<Schedule>> = MutableStateFlow(
        listOf(
            Schedule(
                1,
                "test",
                listOf(1),
                listOf(),
                listOf(),
                LocalDateTime(2024, 7, 1, 11, 20, 0),
                RecurrenceType.NOT_REPEAT,
                "This is a test"
            ), Schedule(
                2,
                "pick up child",
                listOf(2, 3),
                listOf(),
                listOf(),
                LocalDateTime(2024, 7, 2, 11, 40, 0),
                RecurrenceType.WEEKLY,
                "This is pick up child"
            )
        )
    )

    private val _editingContact: MutableStateFlow<Contact?> = MutableStateFlow(null)
    private val _editingSchedule: MutableStateFlow<Schedule?> = MutableStateFlow(null)

    val editingContact: StateFlow<Contact?> = _editingContact.asStateFlow()
    val editingSchedule: StateFlow<Schedule?> = _editingSchedule.asStateFlow()

    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()
    val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

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
        _contacts.update { existingContacts ->
            val isNewContact = existingContacts.find { it.id == contact.id } == null
            if (isNewContact) {
                contacts.value.plus(contact)
            } else {
                existingContacts.map { if (it.id == contact.id) contact else it }
            }
        }
    }

    fun updateSchedule(schedule: Schedule) {
        _schedules.update { existingSchedules ->
            val isNewSchedule = existingSchedules.find { it.id == schedule.id } == null
            if (isNewSchedule) {
                existingSchedules.plus(schedule)
            } else {
                existingSchedules.map {
                    if (it.id == schedule.id) schedule.copy(
                        pendingEvents = listOf()
                    ) else it
                }
            }
        }
    }

    // fun updateScheduleName(name: String) {
    //     _editingSchedule.value?.takeIf { it.name != name }?.copy(name = name)?.let {
    //         _editingSchedule.value = it
    //     }
    // }

    // fun updateScheduleMessage(message: String) {
    //     _editingSchedule.value?.takeIf { it.message != message }?.copy(message = message)?.let {
    //         _editingSchedule.value = it
    //     }
    // }

    // fun updateScheduleSentTime(sentTime: LocalDateTime) {
    //     _editingSchedule.value?.takeIf { it.sentTime != sentTime }?.copy(sentTime = sentTime)?.let {
    //         _editingSchedule.value = it
    //     }
    // }

    // fun updateScheduleDate(date: LocalDate) {
    //     _editingSchedule.value?.takeIf { it.sentTime.date != date }?.let {
    //         val updatedSentTime = LocalDateTime(date, it.sentTime.time)
    //         _editingSchedule.value = it.copy(sentTime = updatedSentTime)
    //     }
    // }

    // fun updateScheduleTime(time: LocalTime) {
    //     _editingSchedule.value?.takeIf { it.sentTime.time != time }?.let {
    //         val updatedSentTime = LocalDateTime(it.sentTime.date, time)
    //         _editingSchedule.value = it.copy(sentTime = updatedSentTime)
    //     }
    // }

    // fun updateScheduleRecurrence(recurrence: RecurrenceType) {
    //     _editingSchedule.value?.takeIf { it.recurrence != recurrence }
    //         ?.copy(recurrence = recurrence)?.let {
    //             _editingSchedule.value = it
    //         }
    // }

    // fun updateScheduleReceivers(receivers: List<Long>) {
    //     _editingSchedule.value?.takeIf { it.receivers != receivers }?.copy(receivers = receivers)
    //         ?.let {
    //             _editingSchedule.value = it
    //         }
    // }

    // fun moveScheduleMember(from: Int, to: Int) {
    //     _editingSchedule.value?.let {
    //         val mutableMembers = it.receivers.toMutableList()
    //         val member = mutableMembers.removeAt(from)
    //         mutableMembers.add(if (to > from) to - 1 else to, member)
    //         _editingSchedule.value = it.copy(receivers = mutableMembers.toList())
    //     }
    // }

    // fun moveContact(from: Int, to: Int) {

    // }

    // private fun startEventLoop() {
    //     viewModelScope.launch {
    //         while (isActive) {
    //             generateEvents()
    //             delay(1 * 60 * 1000)
    //         }
    //     }
    // }

    // private suspend fun generateEvents() {
    //     withContext(Dispatchers.Default) {
    //         val currentMoment: Instant = Clock.System.now()
    //         val now: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())

    //         //TODO consider update pending message dynamically, which mean regenerate it according to the latest schedule
    //         _schedules.value?.map {
    //             when (it.recurrence) {
    //                 RecurrenceType.NOT_REPEAT -> {
    //                     if (it.sentEvents.isEmpty() && it.pendingEvents.isEmpty() && it.sentTime > now) {
    //                         val event = Event(
    //                             System.currentTimeMillis(),
    //                             it.receivers.first(),
    //                             it.message,
    //                             it.sentTime
    //                         )
    //                         it.copy(pendingEvents = listOf(event))
    //                     } else {
    //                         it
    //                     }
    //                 }

    //                 RecurrenceType.DAILY -> {
    //                     if (it.pendingEvents.none { event -> event.sentTime >= now }) {
    //                         val event = Event(
    //                             System.currentTimeMillis(),
    //                             it.receivers.first(),
    //                             it.message,
    //                             if (it.sentTime > now) it.sentTime else LocalDateTime(
    //                                 now.date.plus(
    //                                     DatePeriod(0, 0, 1)
    //                                 ), it.sentTime.time
    //                             )
    //                         )
    //                         it.copy(pendingEvents = listOf(event))
    //                     } else {
    //                         it
    //                     }
    //                 }

    //                 RecurrenceType.WEEKLY -> {
    //                     if (it.pendingEvents.none { event -> event.sentTime >= now }) {
    //                         val offset = it.sentTime.dayOfWeek.ordinal - now.date.dayOfWeek.ordinal
    //                         val event = Event(
    //                             System.currentTimeMillis(),
    //                             it.receivers.first(),
    //                             it.message,
    //                             if (it.sentTime > now) it.sentTime else LocalDateTime(
    //                                 now.date.plus(DatePeriod(0, 0, 7 + offset)), it.sentTime.time
    //                             )
    //                         )
    //                         it.copy(pendingEvents = listOf(event))
    //                     } else {
    //                         it
    //                     }
    //                 }

    //                 RecurrenceType.MONTHLY -> {
    //                     if (it.pendingEvents.none { event -> event.sentTime >= now }) {
    //                         val event = Event(
    //                             System.currentTimeMillis(),
    //                             it.receivers.first(),
    //                             it.message,
    //                             if (it.sentTime > now) it.sentTime else LocalDateTime(
    //                                 now.date.plus(DatePeriod(0, 1, 0)), it.sentTime.time
    //                             )
    //                         )
    //                         it.copy(pendingEvents = listOf(event))
    //                     } else {
    //                         it
    //                     }
    //                 }

    //                 RecurrenceType.Annually -> {
    //                     if (it.pendingEvents.none { event -> event.sentTime >= now }) {
    //                         val event = Event(
    //                             System.currentTimeMillis(),
    //                             it.receivers.first(),
    //                             it.message,
    //                             if (it.sentTime > now) it.sentTime else LocalDateTime(
    //                                 now.date.plus(DatePeriod(1, 0, 0)), it.sentTime.time
    //                             )
    //                         )
    //                         it.copy(pendingEvents = listOf(event))
    //                     } else {
    //                         it
    //                     }
    //                 }

    //                 RecurrenceType.WEEKDAY -> {
    //                     if (it.pendingEvents.none { event -> event.sentTime >= now }) {

    //                         val nextDate = when (now.date.dayOfWeek.ordinal) {
    //                             4 -> {
    //                                 now.date.plus(DatePeriod(0, 0, 3))
    //                             }

    //                             5 -> {
    //                                 now.date.plus(DatePeriod(0, 0, 2))
    //                             }

    //                             else -> {
    //                                 now.date.plus(DatePeriod(0, 0, 1))
    //                             }
    //                         }

    //                         val event = Event(
    //                             System.currentTimeMillis(),
    //                             it.receivers.first(),
    //                             it.message,
    //                             if (it.sentTime > now) it.sentTime else LocalDateTime(
    //                                 nextDate, it.sentTime.time
    //                             )
    //                         )
    //                         it.copy(pendingEvents = listOf(event))
    //                     } else {
    //                         it
    //                     }
    //                 }
    //             }
    //         }?.let { _schedules.postValue(it) }

    //     }

    // }

}