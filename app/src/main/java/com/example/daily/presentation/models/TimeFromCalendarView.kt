package com.example.daily.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class TimeFromCalendarView @Inject constructor(
    var year: Int,
    var month: Int,
    var dayOfMonth: Int
) : Parcelable