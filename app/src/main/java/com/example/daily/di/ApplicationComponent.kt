package com.example.daily.di

import android.content.Context
import com.example.daily.presentation.activity.MainActivity
import dagger.BindsInstance
import dagger.Component

@Component(modules = [DataModule::class, DomainModule::class, ViewModelModel::class, PresentationModule::class])
@ApplicationScope
interface ApplicationComponent {

    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
}