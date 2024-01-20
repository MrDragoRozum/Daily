package com.example.daily.domain.models

data class Task(
    val id: Int = DEFAULT_ID,
    val dateStart: Long,
    val dateFinish: Long,
    val name: String,
    val description: String
)

const val DEFAULT_ID = -1
