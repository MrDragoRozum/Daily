package com.example.daily.domain.repository

import com.example.daily.domain.models.Task
import kotlinx.coroutines.flow.Flow


interface DailyRepository {
    suspend fun addTask(params: Task)
    fun getListTaskSpecificDay(): Flow<List<Task>>
    suspend fun importTasks(uri: String)
    suspend fun exportTasks(uri: String)
    suspend fun requestNewListTaskSpecificDay(startDay: Long, endDay: Long)
}