package com.example.daily.domain.usecase

import com.example.daily.domain.models.Task
import com.example.daily.domain.repository.DailyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetListTaskSpecificDay @Inject constructor(private val repository: DailyRepository) {
    operator fun invoke(): Flow<List<Task>> = repository.getListTaskSpecificDay()

}