package com.zainab.notely.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "checklists",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId")]
)
data class Checklist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val categoryId: Int? = null,
    val createdAt: Long,
    val updatedAt: Long
)