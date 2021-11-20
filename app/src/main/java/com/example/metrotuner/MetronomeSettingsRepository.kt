package com.example.metrotuner

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MetronomeSettingsRepository(context: Context) {
    private val dataBaseDao = MetronomeDatabase.getDatabase(context).metronomeSettingsDao()

    fun getAllSettings(): Flow<List<MetronomeSettingsEntity>> {
        return dataBaseDao.getAll()
    }

    fun saveSettings(settingsEntity: MetronomeSettingsEntity){
        GlobalScope.launch(Dispatchers.IO) {
            dataBaseDao.insert(settingsEntity)
        }
    }
}