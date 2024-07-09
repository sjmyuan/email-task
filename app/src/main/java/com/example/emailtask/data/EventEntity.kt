package com.example.emailtask.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.emailtask.model.Event
import com.example.emailtask.model.Status
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "event")
data class EventEntity(
    @PrimaryKey val eventId: Long,
    val scheduleId: Long,
    val receiverId: Long,
    val receiverName: String,
    val receiverEmail: String,
    val receiverMobile: String,
    val message: String,
    val sentTime: String,
    val status: Int
) {
    fun toEvent() = Event(
        this.eventId,
        this.scheduleId,
        this.receiverId,
        this.receiverName,
        this.receiverEmail,
        this.receiverMobile,
        this.message,
        LocalDateTime.parse(this.sentTime, LocalDateTime.Formats.ISO),
        Status.entries[this.status]
    )

    companion object {
        fun fromEvent(event: Event) = EventEntity(
            event.id,
            event.scheduleId,
            event.receiverId,
            event.receiverName,
            event.receiverEmail,
            event.receiverMobile,
            event.message,
            LocalDateTime.Formats.ISO.format(event.sentTime),
            event.status.ordinal
        )
    }
}