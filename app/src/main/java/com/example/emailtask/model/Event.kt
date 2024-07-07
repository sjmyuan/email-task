package com.example.emailtask.model

import kotlinx.datetime.LocalDateTime

data class Event(
    val id: Long,
    val receiver: Contact,
    val message: String,
    val sentTime: LocalDateTime,
    val status: Status
)
