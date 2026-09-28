package com.zainab.notely.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Query("DELETE FROM categories WHERE id = :id AND isDefault = 0")
    suspend fun deleteCustomById(id: Int): Int

    @Query("SELECT * FROM categories ORDER BY isDefault DESC, id ASC")
    suspend fun getAll(): List<Category>
}