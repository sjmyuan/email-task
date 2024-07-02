package com.example.emailtask.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(vararg events: EventEntity)

    @Update
    suspend fun updateEvents(vararg events: EventEntity)

    @Delete
    suspend fun deleteEvents(vararg events: EventEntity)

    @Query("SELECT * FROM event")
    suspend fun getAll(): List<EventWithReceiver>

    @Query("SELECT * FROM event where status == 'pending'")
    suspend fun getAllPendingEvents(): List<EventWithReceiver>
}