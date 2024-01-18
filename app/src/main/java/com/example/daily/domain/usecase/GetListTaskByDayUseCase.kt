package com.example.daily.domain.usecase

import com.example.daily.domain.models.Task
import kotlinx.coroutines.flow.Flow
import java.sql.Timestamp
import javax.inject.Inject

class GetListTaskByDayUseCase @Inject constructor(private val repository: DailyRepository) {
    operator fun invoke(day: Timestamp): Flow<List<Task>> = repository.getListTaskByDay(day)

}