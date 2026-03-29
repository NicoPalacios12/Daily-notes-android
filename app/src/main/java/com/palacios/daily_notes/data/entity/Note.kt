package com.palacios.daily_notes.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description : String,
    val isCompleted : Boolean = false,
    val category : String = "General",
    val categoryColor : Long = 0xFF6750A4,
    val createdAt : String = "2026-01-11" // Format yyyy-MM-dd
)