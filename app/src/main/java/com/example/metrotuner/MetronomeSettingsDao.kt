package com.example.metrotuner

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MetronomeSettingsDao {

    @Query("SELECT * FROM metronomesettingsentity")
    fun getAll(): Flow<List<MetronomeSettingsEntity>>

    @Insert
    suspend fun insert(settingsEntity: MetronomeSettingsEntity)
}