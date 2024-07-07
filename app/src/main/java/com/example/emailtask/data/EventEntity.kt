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
    val message: String,
    val sentTime: String,
    val status: Int
)

data class EventWithReceiver(
    @Embedded val event: EventEntity,

    @Relation(
        parentColumn = "receiverId",
        entityColumn = "contactId"
    )
    val receiver: ContactEntity
) {
    fun toEvent() =
        Event(
            event.eventId,
            receiver.toContact(),
            event.message,
            LocalDateTime.parse(event.sentTime, LocalDateTime.Formats.ISO),
            Status.entries[event.status]
        )
}