package com.example.daily.domain.usecase

import com.example.daily.domain.models.Task
import com.example.daily.domain.repository.DailyRepository
import kotlinx.coroutines.flow.Flow
import java.sql.Timestamp
import javax.inject.Inject

class GetListTaskByDayUseCase @Inject constructor(private val repository: DailyRepository) {
    operator fun invoke(startDay: Timestamp, endDay: Timestamp): Flow<List<Task>> =
        repository.getListTaskByDay(startDay, endDay)

}