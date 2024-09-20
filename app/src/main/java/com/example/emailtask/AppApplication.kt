package com.example.emailtask

import android.app.Application
import androidx.work.Configuration
import com.example.emailtask.data.AppDatabase
import com.example.emailtask.repository.ContactRepository
import com.example.emailtask.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AppApplication : Application(), Configuration.Provider {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val appDatabase by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val contactRepository by lazy { ContactRepository(appDatabase.contactDao()) }
    val scheduleRepository by lazy {
        ScheduleRepository(
            appDatabase.scheduleDao(),
            appDatabase.eventDao()
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(AppWorkerFactory(scheduleRepository))
            .build()
}