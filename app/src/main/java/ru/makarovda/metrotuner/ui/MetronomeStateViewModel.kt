package ru.makarovda.metrotuner.ui

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.makarovda.metrotuner.utility.EventIntervalCalculator
import java.util.*

/**
 *   ViewModel для описания текущего состояния
 */
class MetronomeStateViewModel: ViewModel() {
    // Количество ударов в минуту
    var bpm: Int = 120

    // Стек с описанием долей в такте
     var accents: Stack<Boolean> = Stack<Boolean>().apply{
        addAll(arrayOf(true, false, false, false))
    }
    private set

    // Количество ударов в такте
    val beats: Int
        get() = accents.size

    // Размер
    var divider: Int = 4
    val accentStr: String
       get() {
            val accentsChars = Array<Char>(accents.size){
                if (accents[it]) 'X' else 'x'
            }
            return String(accentsChars.toCharArray())
       }

    private val _bpmFlow = MutableStateFlow<Int>(120)
    val bpmFlow : StateFlow<Int>
        get() = _bpmFlow

    private val tempCalc = EventIntervalCalculator(10, 6000, 10, viewModelScope) {
        _bpmFlow.value = it
    }


    // Изменение числа ударов в минуту
    // delta - значение, которое необходимо добавить
    fun changeBpm(delta: Int){
        val newBpm = _bpmFlow.value + delta
        if((newBpm > 0) && (newBpm <= 250)){
            _bpmFlow.value = newBpm
        }
    }

    fun setBpmValue(newBpm: Int){
        if((newBpm > 0) && (newBpm <= 250)){
            _bpmFlow.value = newBpm
        }
    }

    fun beatsFromString(beatsStr: String){
        if (beatsStr.length == 0) return
        val accentsArr = Array(beatsStr.length){
            beatsStr[it] == 'X'
        }
        accents = Stack<Boolean>().apply {
            addAll(accentsArr)
        }
    }

    fun changeBeats(delta: Int){
        if(delta < 0) {
            accents.pop()
        } else if(delta > 0) {
            accents.push(false)
        }
    }

    fun tempClickHandler() {
        tempCalc.eventHandler()
    }

}