package ru.makarovda.metrotuner.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.makarovda.metrotuner.data.MetronomeDatabase
import ru.makarovda.metrotuner.data.MetronomeSettingsEntity

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

    fun deleteProfile(settingsEntity: MetronomeSettingsEntity){
        GlobalScope.launch(Dispatchers.IO) {
            dataBaseDao.delete(settingsEntity)
        }
    }
}