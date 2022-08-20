package ru.makarovda.metrotuner

import android.app.Application
import ru.makarovda.metrotuner.data.MetronomeSettingsRepository

class MetroTunerApp: Application() {
    //var repository: MetronomeSettingsRepository? = null

    override fun onCreate() {
        super.onCreate()
        //repository = MetronomeSettingsRepository(this)
    }

}