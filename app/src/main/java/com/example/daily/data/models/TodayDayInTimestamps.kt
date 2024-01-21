package com.example.daily.data.models

import java.sql.Timestamp
import javax.inject.Inject

data class TodayDayInTimestamps @Inject constructor(
    var startDay: Timestamp,
    var endDay: Timestamp
)
