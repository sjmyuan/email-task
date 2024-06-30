package com.example.emailtask.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class AppViewModel : ViewModel() {

    init {
        startEventLoop()
    }

    private val _contacts: MutableLiveData<List<Contact>> = MutableLiveData(
        listOf(
            Contact(1, "Tod1", "1234567890", "john@example.com"),
            Contact(2, "Tod2", "1234567890", "john@example.com"),
            Contact(3, "Tod3", "1234567890", "john@example.com"),
            Contact(4, "Tod4", "1234567890", "john@example.com"),
            Contact(5, "Tod5", "1234567890", "john@example.com"),
        )
    )
    private val _schedules: MutableLiveData<List<Schedule>> = MutableLiveData(
        listOf(
            Schedule(
                1,
                "test",
                listOf(1),
                listOf(),
                listOf(),
                LocalDateTime(2024, 7, 1, 11, 20, 0),
                RecurrenceType.NOT_REPEAT,
                ""
            ), Schedule(
                2,
                "pick up child",
                listOf(2, 3),
                listOf(),
                listOf(),
                LocalDateTime(2024, 7, 2, 11, 40, 0),
                RecurrenceType.WEEKLY,
                ""
            )
        )
    )
    private val _editingContact: MutableLiveData<Contact> = MutableLiveData()
    private val _editingSchedule: MutableLiveData<Schedule> = MutableLiveData()

    val contacts: LiveData<List<Contact>> = _contacts
    val schedules: LiveData<List<Schedule>> = _schedules
    val editingContact: LiveData<Contact> = _editingContact
    val editingSchedule: LiveData<Schedule> = _editingSchedule

    val editingScheduleAndContacts: MediatorLiveData<Pair<Schedule?, List<Contact>?>> =
        MediatorLiveData<Pair<Schedule?, List<Contact>?>>().apply {
            addSource(_editingSchedule) { value = it to _contacts.value }
            addSource(_contacts) { value = _editingSchedule.value to it }
        }

    val schedulesAndContacts: MediatorLiveData<Pair<List<Schedule>?, List<Contact>?>> =
        MediatorLiveData<Pair<List<Schedule>?, List<Contact>?>>().apply {
            addSource(_schedules) { value = it to _contacts.value }
            addSource(_contacts) { value = _schedules.value to it }
        }

    fun setEditingContact(contact: Contact) {
        _editingContact.value = contact
    }

    fun setEditingSchedule(schedule: Schedule) {
        _editingSchedule.value = schedule
    }

    fun updateContact(contact: Contact) {
        val isNewContact = _contacts.value?.find { it.id == contact.id } == null

        if (isNewContact) {
            val newContacts = _contacts.value?.plus(contact) ?: listOf(contact)
            _contacts.value = newContacts
        } else {

            val newContacts =
                _contacts.value?.map { if (it.id == contact.id) contact else it } ?: listOf(
                    contact
                )
            _contacts.value = newContacts
        }

    }

    fun saveEditingSchedule() {
        val isNewSchedule = _schedules.value?.find { it.id == _editingSchedule.value?.id } == null
        if (isNewSchedule) {
            val newSchedules =
                _schedules.value?.plus(_editingSchedule.value) ?: listOf(_editingSchedule.value)
            _schedules.value = newSchedules.mapNotNull { it }
        } else {
            // Clear pending event if the schedule was changed
            val newSchedules = _schedules.value?.map {
                if (it.id == _editingSchedule.value?.id) _editingSchedule.value?.copy(
                    pendingEvents = listOf()
                ) else it
            } ?: listOf(
                _editingSchedule.value?.copy(pendingEvents = listOf())
            )
            _schedules.value = newSchedules.mapNotNull { it }
        }
    }

    fun updateScheduleName(name: String) {
        _editingSchedule.value?.takeIf { it.name != name }?.copy(name = name)?.let {
            _editingSchedule.value = it
        }
    }

    fun updateScheduleMessage(message: String) {
        _editingSchedule.value?.takeIf { it.message != message }?.copy(message = message)?.let {
            _editingSchedule.value = it
        }
    }

    fun updateScheduleSentTime(sentTime: LocalDateTime) {
        _editingSchedule.value?.takeIf { it.sentTime != sentTime }?.copy(sentTime = sentTime)?.let {
            _editingSchedule.value = it
        }
    }

    fun updateScheduleDate(date: LocalDate) {
        _editingSchedule.value?.takeIf { it.sentTime.date != date }?.let {
            val updatedSentTime = LocalDateTime(date, it.sentTime.time)
            _editingSchedule.value = it.copy(sentTime = updatedSentTime)
        }
    }

    fun updateScheduleTime(time: LocalTime) {
        _editingSchedule.value?.takeIf { it.sentTime.time != time }?.let {
            val updatedSentTime = LocalDateTime(it.sentTime.date, time)
            _editingSchedule.value = it.copy(sentTime = updatedSentTime)
        }
    }

    fun updateScheduleRecurrence(recurrence: RecurrenceType) {
        _editingSchedule.value?.takeIf { it.recurrence != recurrence }
            ?.copy(recurrence = recurrence)?.let {
                _editingSchedule.value = it
            }
    }

    fun updateScheduleReceivers(receivers: List<Long>) {
        _editingSchedule.value?.takeIf { it.receivers != receivers }?.copy(receivers = receivers)
            ?.let {
                _editingSchedule.value = it
            }
    }

    fun moveScheduleMember(from: Int, to: Int) {
        _editingSchedule.value?.let {
            val mutableMembers = it.receivers.toMutableList()
            val member = mutableMembers.removeAt(from)
            mutableMembers.add(if (to > from) to - 1 else to, member)
            _editingSchedule.value = it.copy(receivers = mutableMembers.toList())
        }
    }

    fun moveContact(from: Int, to: Int) {

    }

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
            _schedules.value?.map {
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
                            val offset = it.sentTime.dayOfWeek.ordinal - now.date.dayOfWeek.ordinal
                            val event = Event(
                                System.currentTimeMillis(),
                                it.receivers.first(),
                                it.message,
                                if (it.sentTime > now) it.sentTime else LocalDateTime(
                                    now.date.plus(DatePeriod(0, 0, 7 + offset)), it.sentTime.time
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
            }?.let { _schedules.postValue(it) }

        }

    }

}