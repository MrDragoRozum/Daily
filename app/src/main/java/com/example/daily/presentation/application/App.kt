package com.example.daily.presentation.application

import android.app.Application
import com.example.daily.di.DaggerApplicationComponent

class App: Application() {
    val component by lazy {
        DaggerApplicationComponent.factory().create(applicationContext)
    }
}