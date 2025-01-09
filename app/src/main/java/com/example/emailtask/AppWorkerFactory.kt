package com.example.emailtask

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.emailtask.repository.ScheduleRepository
import com.example.emailtask.repository.SettingRepository

class AppWorkerFactory(private val scheduleRepository: ScheduleRepository, private val settingRepository: SettingRepository) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        if (workerClassName == EventGenerator::class.java.name) {
            return EventGenerator(appContext, workerParameters, scheduleRepository)
        }

        if (workerClassName == EventSender::class.java.name) {
            return EventSender(appContext, workerParameters, scheduleRepository, settingRepository)
        }

        return null
    }
}