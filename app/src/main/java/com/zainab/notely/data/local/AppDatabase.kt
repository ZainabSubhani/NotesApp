package com.zainab.notely.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Category::class, Note::class, Checklist::class, ChecklistItem::class,  Reminder::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun noteDao(): NoteDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun checklistItemDao(): ChecklistItemDao

    abstract fun reminderDao(): ReminderDao
}