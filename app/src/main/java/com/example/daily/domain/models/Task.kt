package com.example.daily.domain.models

import java.sql.Timestamp

data class Task(
    val id: Int,
    val dateStart: Timestamp,
    val dateFinish: Timestamp,
    val name: String,
    val description: String
)
