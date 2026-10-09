package com.zainab.notely.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val dueAt: Long,
    val isCompleted: Boolean = false,
    val createdAt: Long
)