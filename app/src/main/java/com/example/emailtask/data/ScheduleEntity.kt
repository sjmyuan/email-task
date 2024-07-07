package com.example.emailtask.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.emailtask.model.Event
import com.example.emailtask.model.RecurrenceType
import com.example.emailtask.model.Schedule
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "schedule")
data class ScheduleEntity(
    @PrimaryKey val scheduleId: Long,
    val name: String,
    val sentTime: String,
    val recurrence: Int,
    val message: String,
)

@Entity(tableName = "schedule_contact_mapping", primaryKeys = ["scheduleId", "contactId"])
data class ScheduleContactCrossRef(
    val scheduleId: Long,
    val contactId: Long
)

data class ScheduleWithReceiversAndEvents(
    @Embedded val schedule: ScheduleEntity,
    @Relation(
        parentColumn = "scheduleId",
        entityColumn = "contactId",
        associateBy = Junction(ScheduleContactCrossRef::class)
    )
    val receivers: List<ContactEntity>,

    @Relation(
        parentColumn = "scheduleId",
        entityColumn = "scheduleId"
    )
    val events: List<EventWithReceiver>
) {
    fun toSchedule() = Schedule(
        schedule.scheduleId,
        schedule.name,
        receivers.map { it.toContact() },
        events.map { it.toEvent() },
        LocalDateTime.parse(schedule.sentTime, LocalDateTime.Formats.ISO),
        RecurrenceType.entries[schedule.recurrence],
        schedule.message
    )

    companion object {
        fun fromSchedule(source: Schedule) =
            ScheduleEntity(
                source.id,
                source.name,
                LocalDateTime.Formats.ISO.format(source.sentTime),
                source.recurrence.ordinal,
                source.message
            )


        fun toEventEntity(source: Schedule, event: Event) = EventEntity(
            event.id,
            source.id,
            event.receiver.id,
            event.message,
            LocalDateTime.Formats.ISO.format(event.sentTime),
            event.status.ordinal
        )

    }

}
