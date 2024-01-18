package com.example.daily.domain.usecase

import javax.inject.Inject

class ExportTasksUseCase @Inject constructor(private val repository: DailyRepository) {
    suspend operator fun invoke(uri: String) {
        repository.exportTasks(uri)
    }
}