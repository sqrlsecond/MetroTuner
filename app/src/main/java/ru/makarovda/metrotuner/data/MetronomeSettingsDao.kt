package ru.makarovda.metrotuner.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MetronomeSettingsDao {

    @Query("SELECT * FROM metronomesettingsentity")
    fun getAll(): Flow<List<MetronomeSettingsEntity>>

    @Insert
    suspend fun insert(settingsEntity: MetronomeSettingsEntity)

    @Delete
    suspend fun delete(settingsEntity: MetronomeSettingsEntity)
}