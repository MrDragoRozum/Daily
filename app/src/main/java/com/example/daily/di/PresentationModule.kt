package com.example.daily.di

import dagger.Module
import dagger.Provides
import java.util.Calendar

@Module
class PresentationModule {
    @Provides
    fun provideCalendar(): Calendar = Calendar.getInstance()
}