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