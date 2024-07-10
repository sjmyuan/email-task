package com.example.emailtask.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(vararg schedules: ScheduleEntity)

    @Update
    suspend fun updateSchedules(vararg schedules: ScheduleEntity)

    @Delete
    suspend fun deleteSchedules(vararg schedules: ScheduleEntity)

    @Query("DELETE FROM schedule_contact_mapping where scheduleId = :scheduleId")
    suspend fun deleteScheduleReceivers(scheduleId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleReceivers(vararg receivers: ScheduleContactCrossRef)

    @Transaction
    @Query("SELECT * FROM schedule")
    fun getAll(): Flow<List<ScheduleWithReceiversAndEvents>>
}