package com.example.daily.domain.usecase

import com.example.daily.domain.repository.DailyRepository
import javax.inject.Inject

class ImportTasksUseCase @Inject constructor(private val repository: DailyRepository) {
    suspend operator fun invoke(uri: String) {
        repository.importTasks(uri)
    }
}