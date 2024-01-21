package com.example.daily.di

import dagger.Module
import dagger.Provides
import java.util.Calendar

@Module
object PresentationModule {
    @Provides
    fun provideCalendar(): Calendar = Calendar.getInstance()
}