package ru.makarovda.metrotuner.utility

import androidx.collection.CircularIntArray
import kotlin.math.abs

class Averager(private val bufferCapacity: Int) {

    private val buffer = CircularIntArray(bufferCapacity)
    var average: Double = 0.0
        private set


    fun push(value: Int){

        val currentSize = buffer.size() // Текущий размер буфера

        if (bufferCapacity > currentSize){
            average = (average * currentSize.toDouble() + value.toDouble()) / (currentSize + 1).toDouble()
        }else {
            average -= (buffer.popFirst().toDouble() - value.toDouble()) / currentSize.toDouble()
        }
        buffer.addLast(value)
    }

    fun cleanBuffer(){
        average = 0.0
        buffer.clear()
    }
}