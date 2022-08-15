package ru.makarovda.metrotuner.utility

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EventIntervalCalculator(bufferCapacity: Int,
                              private val maxInterval: Int,
                              private val timeStep: Long = 1,
                              private val coroutineScope: CoroutineScope,
                              private val intervalHandler: (Int)->Unit) {

    private val averager = Averager(bufferCapacity)
    private var isCounting: Boolean = false
    private var counter: Int = 0

    //Обработка события для расчёта интервала между двумя событиями
    fun eventHandler() {
        if (!isCounting) {
            coroutineScope.launch(Dispatchers.Default){
                isCounting = true
                while(counter < maxInterval){
                    delay(timeStep)
                    counter++
                }
                isCounting = false
            }
        } else {
            averager.push(counter)
            counter = 0
            intervalHandler(averager.average.toInt())
        }
    }
}