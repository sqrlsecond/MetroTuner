package com.example.metrotuner

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MetronomeSettingsEntity::class], version = 1)
abstract class MetronomeDatabase: RoomDatabase() {
    abstract fun metronomeSettingsDao(): MetronomeSettingsDao

    companion object {
        private var INSTANCE: MetronomeDatabase? = null

        fun getDatabase(context: Context): MetronomeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MetronomeDatabase::class.java,
                "metronome_presets").build()
                INSTANCE = instance

                instance
            }
        }
    }
}