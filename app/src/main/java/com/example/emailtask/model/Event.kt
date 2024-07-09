package com.example.emailtask.model

import kotlinx.datetime.LocalDateTime

data class Event(
    val id: Long,
    val scheduleId: Long,
    val receiverId: Long,
    val receiverName: String,
    val receiverEmail: String,
    val receiverMobile: String,
    val message: String,
    val sentTime: LocalDateTime,
    val status: Status
)
