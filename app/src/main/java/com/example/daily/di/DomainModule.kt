package com.example.daily.di

import com.example.daily.data.repository.DailyRepositoryImpl
import com.example.daily.domain.repository.DailyRepository
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {

    @Binds
    @ApplicationScope
    fun bindDailyRepository(impl: DailyRepositoryImpl): DailyRepository
}