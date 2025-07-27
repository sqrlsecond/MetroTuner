package ru.makarovda.metrotuner.domain.metronome

import java.util.Stack

class MetronomeBeats(
    beatsArray: List<Boolean> = listOf(true),
) {
    private val _beats: Stack<Boolean> = Stack<Boolean>().apply {
        addAll(beatsArray)
    }
    val beats
        get() = _beats.toList()

    val size
        get() = _beats.size

    override fun toString(): String {
        val accentsChars = Array(_beats.size){
            if (_beats[it]) 'X' else 'x'
        }
        return String(accentsChars.toCharArray())
    }

    fun plusOne() {
        if (_beats.size >= 12) return
        _beats.push(false)
    }

    fun minusOne() {
        if (_beats.size <= 1) return
        _beats.pop()
    }

    fun changeAccent(index: Int){
        if (index >= 0 && index < _beats.size){
            _beats[index] = !_beats[index]
        }
    }

    companion object {
        fun fromString(beatsStr: String): MetronomeBeats {
            if (beatsStr.length == 0) return MetronomeBeats()
            val accentsArr = Array(beatsStr.length){
                beatsStr[it] == 'X'
            }
            return MetronomeBeats(accentsArr.toList())
        }

        fun convertListToString(beatsList: List<Boolean>): String {
            val accentsChars = Array(beatsList.size){
                if (beatsList[it]) 'X' else 'x'
            }
            return String(accentsChars.toCharArray())
        }
    }
}