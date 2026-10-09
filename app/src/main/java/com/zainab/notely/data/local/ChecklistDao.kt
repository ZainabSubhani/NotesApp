package com.zainab.notely.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ChecklistDao {
    @Insert
    suspend fun insert(checklist: Checklist): Long

    @Update
    suspend fun update(checklist: Checklist)

    @Query("DELETE FROM checklists WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Query("SELECT * FROM checklists WHERE id = :id")
    suspend fun getById(id: Int): Checklist?

    @Query("SELECT * FROM checklists ORDER BY updatedAt DESC")
    suspend fun getAll(): List<Checklist>
}
