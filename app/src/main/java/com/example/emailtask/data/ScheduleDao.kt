package com.example.emailtask.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(vararg schedules: ScheduleEntity)

    @Update
    suspend fun updateSchedules(vararg schedules: ScheduleEntity)

    @Delete
    suspend fun deleteSchedules(vararg schedules: ScheduleEntity)

    @Transaction
    @Query("SELECT * FROM schedule")
    fun getAll(): Flow<List<ScheduleWithReceiversAndEvents>>

}