package com.example.daily.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskDbModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val dateStart: Long,
    val dateFinish: Long,
    val name: String,
    val description: String
)
