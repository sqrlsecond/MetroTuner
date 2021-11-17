package com.example.metrotuner

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Класс для взаимодействия с репозиторием для доступа к списку сохраненных профилей
 */

class MetronomeSettingsVM(private val repository: MetronomeSettingsRepository): ViewModel() {

     var profilesList = repository.getAllSettings()

    fun addProfile(entity: MetronomeSettingsEntity){
        viewModelScope.launch {
            repository.saveSettings(entity)
        }
    }
}

class MetronomeSettingsVMFactory(private val repository: MetronomeSettingsRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MetronomeSettingsVM::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MetronomeSettingsVM(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}