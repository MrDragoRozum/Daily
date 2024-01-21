package com.example.daily.data.externals

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskJson(
    val id: Int,
    @SerialName("date_start") val dateStart: Long,
    @SerialName("date_finish") val dateFinish: Long,
    val name: String,
    val description: String
)
