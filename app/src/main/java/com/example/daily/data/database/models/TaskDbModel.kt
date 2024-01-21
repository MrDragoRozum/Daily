package com.example.daily.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskDbModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "date_start") val dateStart: Long,
    @ColumnInfo(name = "date_finish") val dateFinish: Long,
    val name: String,
    val description: String
)
