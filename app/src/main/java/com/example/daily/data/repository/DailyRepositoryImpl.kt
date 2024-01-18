package com.example.daily.data.repository

import com.example.daily.domain.models.Task
import com.example.daily.domain.usecase.DailyRepository
import kotlinx.coroutines.flow.Flow
import java.sql.Timestamp
import javax.inject.Inject

class DailyRepositoryImpl @Inject constructor(): DailyRepository {
    override fun addTask(params: Task) {
        TODO("Not yet implemented")
    }

    override fun getListTaskByDay(day: Timestamp): Flow<List<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun importTasks(uri: String) {
        TODO("Not yet implemented")
    }

    override suspend fun exportTasks(uri: String) {
        TODO("Not yet implemented")
    }
}