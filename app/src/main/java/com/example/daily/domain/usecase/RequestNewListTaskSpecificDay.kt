package com.example.daily.domain.usecase

import com.example.daily.domain.repository.DailyRepository
import javax.inject.Inject

class RequestNewListTaskSpecificDay @Inject constructor(private val repository: DailyRepository) {
    suspend operator fun invoke(startDay: Long, endDay: Long) {
        repository.requestNewListTaskSpecificDay(startDay, endDay)
    }
}