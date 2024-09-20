package com.example.emailtask.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.emailtask.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(vararg events: EventEntity)

    @Update
    suspend fun updateEvents(vararg events: EventEntity)

    @Delete
    suspend fun deleteEvents(vararg events: EventEntity)

    @Query("DELETE FROM event where scheduleId = :scheduleId")
    suspend fun deleteEventsByScheduleId(scheduleId: Long)

    @Query("SELECT * FROM event")
    fun getAll(): Flow<List<EventEntity>>

    @Query("SELECT * FROM event where status == 'pending'")
    fun getAllPendingEvents(): Flow<List<EventEntity>>
}