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
    val contactId: Long,
    val receiverIndex: Int
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
    val receiverWithIndexes: List<ScheduleContactCrossRef>,

    @Relation(
        parentColumn = "scheduleId",
        entityColumn = "scheduleId"
    )
    val events: List<EventEntity>
) {
    fun toSchedule() = Schedule(
        schedule.scheduleId,
        schedule.name,
        receivers.sortedBy { receiver -> receiverWithIndexes.find { it.contactId == receiver.contactId }?.receiverIndex }
            .map { it.toContact() },
        events.map { it.toEvent() },
        LocalDateTime.parse(schedule.sentTime, LocalDateTime.Formats.ISO),
        RecurrenceType.entries[schedule.recurrence],
        schedule.message
    )

    companion object {
        private fun fromSchedule(source: Schedule) =
            ScheduleEntity(
                source.id,
                source.name,
                LocalDateTime.Formats.ISO.format(source.sentTime),
                source.recurrence.ordinal,
                source.message
            )

        fun fromSchedule1(source: Schedule) =
            ScheduleWithReceiversAndEvents(
                schedule = fromSchedule(source),
                receivers = source.receivers.map { ContactEntity.fromContact(it) },
                receiverWithIndexes = source.receivers.mapIndexed { index, element ->
                    ScheduleContactCrossRef(
                        source.id,
                        element.id,
                        index
                    )
                },
                events = source.events.map { EventEntity.fromEvent(it) }
            )
    }

}
