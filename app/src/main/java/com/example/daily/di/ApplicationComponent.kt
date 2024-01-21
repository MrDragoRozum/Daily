package com.example.daily.di

import android.content.Context
import com.example.daily.presentation.activity.MainActivity
import com.example.daily.presentation.activity.TaskActivity
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [DataModule::class, DomainModule::class, ViewModelModel::class,
        PresentationModule::class, DispatcherModule::class]
)
@ApplicationScope
interface ApplicationComponent {

    fun inject(activity: MainActivity)
    fun inject(activity: TaskActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
}