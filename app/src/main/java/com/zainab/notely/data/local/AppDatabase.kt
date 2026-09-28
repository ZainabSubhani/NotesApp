package com.zainab.notely.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Category::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}