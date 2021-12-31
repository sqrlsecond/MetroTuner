package com.example.metrotuner

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MetronomeStateViewModel: ViewModel() {

    var bpm: Int = 120
    var beats: Int = 4
        set(value) {
            if ((value > 0) && (value < 17)) field = value
        }
    var divider: Int = 4
    var accent: String = "1"
    val bpmFlow = MutableStateFlow<Int>(120)

    // Изменение числа ударов в минуту
    // delta - значение, которое необходимо добавить
    fun changeBpm(delta: Int){
        val newBpm = bpmFlow.value + delta
        if((newBpm > 0) && (newBpm <= 250)){
            bpmFlow.value = newBpm
        }
    }

    fun setBpmValue(newBpm: Int){
        if((newBpm > 0) && (newBpm <= 250)){
            bpmFlow.value = newBpm
        }
    }
}