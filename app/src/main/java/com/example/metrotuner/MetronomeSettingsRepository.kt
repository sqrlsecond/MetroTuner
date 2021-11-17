package com.example.metrotuner

import android.content.Context
import kotlinx.coroutines.flow.Flow

class MetronomeSettingsRepository(context: Context) {
    private val dataBaseDao = MetronomeDatabase.getDatabase(context).metronomeSettingsDao()

    fun getAllSettings(): Flow<List<MetronomeSettingsEntity>> {
        return dataBaseDao.getAll()
    }

    fun saveSettings(settingsEntity: MetronomeSettingsEntity){
        dataBaseDao.insert(settingsEntity)
    }
}