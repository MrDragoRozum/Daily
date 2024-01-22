package com.example.daily.di

import com.example.daily.presentation.models.TimeFromCalendarView
import dagger.Module
import dagger.Provides
import java.util.Calendar

@Module
object PresentationModule {
    @Provides
    fun provideCalendar(): Calendar = Calendar.getInstance()

    @Provides
    fun provideTimeFromCalendarView(calendar: Calendar): TimeFromCalendarView =
        TimeFromCalendarView(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH),
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        )
}