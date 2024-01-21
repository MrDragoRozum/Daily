package com.example.daily.di

import androidx.lifecycle.ViewModel
import com.example.daily.presentation.viewModel.MainViewModel
import com.example.daily.presentation.viewModel.TaskViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModel {

    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Binds
    fun bindMainViewModel(impl: MainViewModel): ViewModel

    @IntoMap
    @ViewModelKey(TaskViewModel::class)
    @Binds
    fun bindTaskViewModel(impl: TaskViewModel): ViewModel
}