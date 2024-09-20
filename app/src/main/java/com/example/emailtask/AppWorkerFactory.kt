package com.example.emailtask

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.emailtask.repository.ScheduleRepository

class AppWorkerFactory(private val repository: ScheduleRepository) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        if (workerClassName == EventGenerator::class.java.name) {
            return EventGenerator(appContext, workerParameters, repository)
        }

        if (workerClassName == EventSender::class.java.name) {
            return EventSender(appContext, workerParameters, repository)
        }

        return null
    }
}