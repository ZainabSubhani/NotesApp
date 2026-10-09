package com.zainab.notely.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ReminderDao {
    @Insert
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(reminder: Reminder)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Int): Reminder?

    @Query("SELECT * FROM reminders ORDER BY dueAt ASC")
    suspend fun getAll(): List<Reminder>
}