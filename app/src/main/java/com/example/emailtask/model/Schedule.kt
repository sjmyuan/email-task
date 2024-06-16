package com.example.emailtask.model

import kotlinx.datetime.LocalDateTime

data class Schedule(
    val id: Long,
    val name: String,
    val receivers: List<Long>,
    val sentEvents: List<Event>,
    val pendingEvents: List<Event>,
    val sentTime: LocalDateTime,
    val recurrence: RecurrenceType,
    val message: String,
)
