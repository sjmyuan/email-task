package com.example.emailtask.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(vararg contacts: ContactEntity)

    @Update
    suspend fun updateContacts(vararg contacts: ContactEntity)

    @Delete
    suspend fun deleteContacts(vararg contacts: ContactEntity)

    @Query("SELECT * FROM contact")
    suspend fun getAll(id: Int): List<ContactEntity>
}