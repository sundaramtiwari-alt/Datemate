package com.example.datemate

import android.app.Application

class DateMateApplication : Application() {

    companion object {
        lateinit var instance: DateMateApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}