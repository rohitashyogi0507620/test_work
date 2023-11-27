package com.example.myapplicationtest

import android.app.Application
import com.google.android.material.color.DynamicColors

class application:Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}