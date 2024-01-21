package com.example.daily.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeFromCalendarView(
    var year: Int = DEFAULT_VALUE,
    var month: Int = DEFAULT_VALUE,
    var dayOfMonth: Int = DEFAULT_VALUE
): Parcelable {
    companion object {
        const val DEFAULT_VALUE = -1
    }
}