package com.example.daily.di

import android.content.Context
import com.example.daily.data.database.TaskDatabase
import com.example.daily.data.models.TodayDayInTimestamps
import dagger.Module
import dagger.Provides
import java.sql.Timestamp
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

@Module
object DataModule {
    private const val DAY_IN_MILLIS = 86400000
    private val HOUR_IN_MILLIS = TimeZone.getDefault().rawOffset
    @Provides
    @ApplicationScope
    fun provideTaskDao(context: Context) = TaskDatabase.getInstance(context).taskDao()

    @Provides
    fun provideTodayDataInTimestamps(calendar: Calendar): TodayDayInTimestamps {
        val todayDayInMillis = calendar.timeInMillis
        val startDay = Timestamp(calculateTime(todayDayInMillis))
        val endDay = Timestamp(startDay.time + DAY_IN_MILLIS)
        return TodayDayInTimestamps(startDay, endDay)
    }

    @Provides
    fun provideRawOffset(): Int = HOUR_IN_MILLIS

    @Provides
    fun provideDate(): Date = Date()

    private fun calculateTime(time: Long): Long =
        (time / DAY_IN_MILLIS) * DAY_IN_MILLIS - HOUR_IN_MILLIS
}