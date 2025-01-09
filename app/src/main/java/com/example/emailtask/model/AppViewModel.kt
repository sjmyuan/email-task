package com.example.emailtask.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import arrow.core.raise.option
import arrow.core.toOption
import com.example.emailtask.repository.ContactRepository
import com.example.emailtask.repository.ScheduleRepository
import com.example.emailtask.repository.SettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.Session
import javax.mail.Transport

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppViewModel(
    private val contactRepository: ContactRepository,
    private val scheduleRepository: ScheduleRepository,
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val HOST_KEY = stringPreferencesKey("smtp.host")
    private val PORT_KEY = intPreferencesKey("smtp.port")
    private val EMAIL_KEY = stringPreferencesKey("smtp.email")
    private val PASSWORD_KEY = stringPreferencesKey("smtp.password")

    private val _editingContact: MutableStateFlow<Contact?> = MutableStateFlow(null)
    private val _editingSchedule: MutableStateFlow<Schedule?> = MutableStateFlow(null)
    private val _editingSMTPConfig: MutableStateFlow<SMTPConfig> =
        MutableStateFlow(SMTPConfig("", 465, "", ""))
    private val _isValidSMTPConfig: MutableStateFlow<Boolean?> = MutableStateFlow(null)

    val editingContact: StateFlow<Contact?> = _editingContact.asStateFlow()
    val editingSchedule: StateFlow<Schedule?> = _editingSchedule.asStateFlow()
    val editingSMTPConfig: StateFlow<SMTPConfig> = _editingSMTPConfig.asStateFlow()
    val isValidSMTPConfig: StateFlow<Boolean?> = _isValidSMTPConfig.asStateFlow()

    val contacts: StateFlow<List<Contact>> =
        contactRepository.allContacts.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())
    val schedules: StateFlow<List<Schedule>> =
        scheduleRepository.allSchedules.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())


    val smtp: StateFlow<SMTPConfig?> =
        settingRepository.smtp.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun updateSMTP(smtpConfig: SMTPConfig) {
        viewModelScope.launch {
            settingRepository.updateSMTP(smtpConfig)
        }
    }

    fun testSMTPConfig(smtpConfig: SMTPConfig) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val props = Properties()
                props["mail.smtp.ssl.enable"] = "true"
                props["mail.smtp.auth"] = "true"
                val session: Session = Session.getInstance(
                    props,
                    null
                )
                val transport: Transport = session.getTransport("smtp")
                transport.connect(
                    smtpConfig.host,
                    smtpConfig.port,
                    smtpConfig.email,
                    smtpConfig.password
                )
                transport.close()
                _isValidSMTPConfig.update { _ -> true }
            } catch (e: Exception) {
                _isValidSMTPConfig.update { _ -> false }
            }

        }
    }

    fun setEditingContact(contact: Contact?) {
        _editingContact.update { _ -> contact }
    }

    fun setEditingSchedule(schedule: Schedule?) {
        _editingSchedule.update { _ -> schedule }
    }

    fun setEditingSMTPConfig(smptConfig: SMTPConfig) {
        _editingSMTPConfig.update { _ -> smptConfig }
        _isValidSMTPConfig.update { _ -> null }
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
    private val scheduleRepository: ScheduleRepository,
    private val settingRepository: SettingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return AppViewModel(
                contactRepository,
                scheduleRepository,
                settingRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
