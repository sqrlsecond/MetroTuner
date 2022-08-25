package ru.makarovda.metrotuner.utility

class EventIntervalCalculator(bufferCapacity: Int,
                              private val maxInterval: Int,
                              timeStep: Long = 1,
                              private val intervalHandler: (Int)->Unit) {

    private val averager = Averager(bufferCapacity)
    private var isCounting: Boolean = false
    private var timeElapsed: Long = 0
    /// Коэффициент пропорциональности между временем в нс и заданным интервалом в мс
    private val kp = 1_000_000 * timeStep

    //Обработка события для расчёта интервала между двумя событиями
    fun eventHandler() {
        if (!isCounting) {
            timeElapsed = System.nanoTime() / kp //преобразование наносекунд в единицы интервала, который задаётся в милисекундах
            isCounting = true
        } else {
            val newTime = System.nanoTime() / kp
            if ((newTime - timeElapsed) >= maxInterval){
                averager.cleanBuffer()
                isCounting = false
                return
            }
            averager.push((newTime - timeElapsed).toInt())
            timeElapsed = newTime
            intervalHandler(averager.average.toInt())
        }
    }
}