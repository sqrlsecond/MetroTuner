package ru.makarovda.metrotuner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.makarovda.metrotuner.utility.EventIntervalCalculator
import java.util.*
import kotlin.math.round

/**
 *   ViewModel для описания текущего состояния
 */
class MetronomeStateViewModel: ViewModel() {

    enum class DeltaBeats{
        PLUS_ONE,
        MINUS_ONE
    }

    // Количество ударов в минуту
    var bpm: Int = 120

    // Стек с описанием долей в такте
    private var _accents: Stack<Boolean> = Stack<Boolean>().apply{
        addAll(arrayOf(true, false, false, false))
    }
    val accents: List<Boolean>
        get() = _accents

    // Количество ударов в такте
    val beats: Int
        get() = _accents.size

    // Размер
    var divider: Int = 4
    val accentStr: String
       get() {
            val accentsChars = Array<Char>(_accents.size){
                if (_accents[it]) 'X' else 'x'
            }
            return String(accentsChars.toCharArray())
       }

    private val _bpmFlow = MutableStateFlow<Int>(120)
    val bpmFlow : StateFlow<Int>
        get() = _bpmFlow

    private val tempCalc = EventIntervalCalculator(10, 6000, 10) {
        setBpmValue(round(6000.0 / it.toDouble()).toInt())
    }

    private val beatsObservers: ArrayList<IBeatsObserver> = ArrayList<IBeatsObserver>()

    // Изменение числа ударов в минуту
    // delta - значение, которое необходимо добавить
    fun changeBpm(delta: Int){
        val newBpm = _bpmFlow.value + delta
        if((newBpm > 0) && (newBpm <= 250)){
            _bpmFlow.value = newBpm
        }
    }

    fun setBpmValue(newBpm: Int){
        var bpmValue = newBpm
        if(newBpm < 1){
            bpmValue = 1
        } else if(newBpm > 250){
            bpmValue = 250
        }
        _bpmFlow.value = bpmValue
    }

    fun beatsFromString(beatsStr: String){
        if (beatsStr.length == 0) return
        val accentsArr = Array(beatsStr.length){
            beatsStr[it] == 'X'
        }
        _accents = Stack<Boolean>().apply {
            addAll(accentsArr)
        }
    }

    fun changeBeats(delta: DeltaBeats){
        when (delta){
            DeltaBeats.MINUS_ONE -> {if(_accents.size > 1) _accents.pop()}
            DeltaBeats.PLUS_ONE-> {if(_accents.size < 12) _accents.push(false)}
        }
        notifyBeatsObservers()
    }

    fun tempClickHandler() {
        tempCalc.eventHandler()
    }

    fun changeAccent(index: Int){
        if (index >= 0 && index < _accents.size){
            _accents[index] = !_accents[index]
            notifyBeatsObservers()
        }
    }

    fun addBeatsObserver(obs: IBeatsObserver){
        beatsObservers.add(obs)
        obs.notifyBeatsObserver(_accents)
    }

    fun removeBeatsObserver(obs: IBeatsObserver){
        beatsObservers.remove(obs)
    }

    private fun notifyBeatsObservers(){
        beatsObservers.forEach{
            it.notifyBeatsObserver(_accents)
        }
    }
}