package com.example.daily.di

import android.content.Context
import com.example.daily.data.database.TaskDatabase
import dagger.Module
import dagger.Provides

@Module
abstract class DataModule {
    @Provides
    @ApplicationScope
    fun provideTaskDao(context: Context) = TaskDatabase.getInstance(context).taskDao()
}