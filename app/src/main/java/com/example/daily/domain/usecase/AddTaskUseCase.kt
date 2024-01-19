package com.example.daily.domain.usecase

import com.example.daily.domain.models.Task
import com.example.daily.domain.repository.DailyRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(private val repository: DailyRepository) {
    suspend operator fun invoke(params: Task) {
        repository.addTask(params)
    }
}