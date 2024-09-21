package com.example.emailtask.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.emailtask.repository.ContactRepository
import com.example.emailtask.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
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

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            scheduleRepository.deleteContact(contact)
            contactRepository.delete(contact)
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.insertSchedule(schedule)
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(schedule)
        }
    }

}

class App1ViewModelFactory(
    private val contactRepository: ContactRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(contactRepository, scheduleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
