package ru.makarovda.metrotuner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Класс для хранения настроек метронома в БД
 */
@Entity
data class MetronomeSettingsEntity(
    /**
     * Название сохраняемого набора настроек
     */
    @PrimaryKey
    val name: String,

    /**
     * Количество ударов в минуту
     */
    val bpm: Int,

    /**
     * Количество долей в такте
     */
    val beats: Int,

    /**
     * Акценты
     */
    val accent: String
)
