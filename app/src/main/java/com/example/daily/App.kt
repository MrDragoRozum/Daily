package com.example.daily

import android.app.Application
import com.example.daily.di.DaggerApplicationComponent

class App: Application() {
    val component by lazy {
        DaggerApplicationComponent.factory().create(applicationContext)
    }
}