package com.zainab.notely.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ChecklistItemDao {
    @Insert
    suspend fun insert(item: ChecklistItem): Long

    @Update
    suspend fun update(item: ChecklistItem)

    @Query("DELETE FROM checklist_items WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Query("SELECT * FROM checklist_items WHERE checklistId = :checklistId ORDER BY position ASC")
    suspend fun getForChecklist(checklistId: Int): List<ChecklistItem>
}