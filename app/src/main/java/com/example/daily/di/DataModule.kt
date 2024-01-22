package com.example.daily.di

import android.content.Context
import com.example.daily.data.database.TaskDatabase
import com.example.daily.data.models.TodayDayInTimestamps
import dagger.Module
import dagger.Provides
import java.sql.Timestamp
import java.util.Calendar
import java.util.Date

@Module
object DataModule {

    @Provides
    @ApplicationScope
    fun provideTaskDao(context: Context) = TaskDatabase.getInstance(context).taskDao()

    @Provides
    fun provideTodayDataInTimestamps(calendar: Calendar): TodayDayInTimestamps {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, WITHOUT_HOUR)
            set(Calendar.MINUTE, WITHOUT_MINUTE)
            set(Calendar.SECOND, WITHOUT_SECOND)
            set(Calendar.MILLISECOND, WITHOUT_MILLISECOND)
        }
        val todayDayInMillis = calendar.timeInMillis
        val startDay = Timestamp(todayDayInMillis)
        val endDay = Timestamp(startDay.time + DAY_IN_MILLIS)
        return TodayDayInTimestamps(startDay, endDay)
    }

    @Provides
    fun provideDate(): Date = Date()

    private const val DAY_IN_MILLIS = 86400000
    private const val WITHOUT_MINUTE = 0
    private const val WITHOUT_SECOND = 0
    private const val WITHOUT_MILLISECOND = 0
    private const val WITHOUT_HOUR = 0

}