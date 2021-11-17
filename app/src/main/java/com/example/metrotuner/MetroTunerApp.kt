package com.example.metrotuner

import android.app.Application

class MetroTunerApp: Application() {
    var repository: MetronomeSettingsRepository? = null

    override fun onCreate() {
        super.onCreate()
        repository = MetronomeSettingsRepository(this)
    }

}