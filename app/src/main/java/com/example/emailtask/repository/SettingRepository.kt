package com.example.emailtask.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewModelScope
import arrow.core.raise.option
import arrow.core.toOption
import com.example.emailtask.model.SMTPConfig
import com.example.emailtask.model.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingRepository(val context: Context) {
    private val HOST_KEY = stringPreferencesKey("smtp.host")
    private val PORT_KEY = intPreferencesKey("smtp.port")
    private val EMAIL_KEY = stringPreferencesKey("smtp.email")
    private val PASSWORD_KEY = stringPreferencesKey("smtp.password")

    val smtp: Flow<SMTPConfig?> = context.dataStore.data.map { preferences ->
        val host = preferences[HOST_KEY].toOption()
        val port = preferences[PORT_KEY].toOption()
        val email = preferences[EMAIL_KEY].toOption()
        val password = preferences[PASSWORD_KEY].toOption()

        option { SMTPConfig(host.bind(), port.bind(), email.bind(), password.bind()) }.getOrNull()
    }

    suspend fun updateSMTP(smtpConfig: SMTPConfig) {
        context.dataStore.edit { preferences ->
            preferences[HOST_KEY] = smtpConfig.host
            preferences[PORT_KEY] = smtpConfig.port
            preferences[EMAIL_KEY] = smtpConfig.email
            preferences[PASSWORD_KEY] = smtpConfig.password
        }

    }
}