package com.example.daily.domain.repository

import com.example.daily.domain.models.Task
import kotlinx.coroutines.flow.Flow
import java.sql.Timestamp


interface DailyRepository {
    suspend fun addTask(params: Task)
    fun getListTaskByDay(startDay: Timestamp, endDay: Timestamp): Flow<List<Task>>
    suspend fun importTasks(uri: String)
    suspend fun exportTasks(uri: String)
}