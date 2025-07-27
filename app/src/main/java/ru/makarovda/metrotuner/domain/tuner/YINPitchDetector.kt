package ru.makarovda.metrotuner.domain.tuner

import kotlin.math.abs
import kotlin.math.PI

class YINPitchDetection {

    private val maxLag = (44100.0 / 50.0).toInt() // 50 Hz - the lowest frequency

    private val windowSize : Int = 5 * maxLag //integration window

    private val frameSize = 2 * windowSize

    private var diffFuncData = DoubleArray(maxLag)

    private var cmndfData = DoubleArray(maxLag){1.0}

    private val frame = ShortArray(frameSize)

    private var amplitudeThreshold: Int = 300
    set(value){
        field = value
        avgSignalThreshold = (2 * amplitudeThreshold.toDouble() / PI).toInt()
    }

    private var avgSignalThreshold = (2 * amplitudeThreshold.toDouble() / PI).toInt()


    /**
     * @brief Рассчёт частоты сигнала
     */
    fun detectFrequency(signal : ShortArray): Double {

        if (signal.size < frameSize * 2) {
            return 0.0
        }

        if(signal.maxOrNull()!! < 300){
            return 0.0
        }

        val frameCount = (signal.size) / frameSize

        var averageFreq = 0.0

        var counter = 0
        val freqs = ArrayList<Double>(frameCount)
        for (i in 0 until frameCount) {
            for (j in 0 until frameSize) {
                frame[j] = signal[i * frameSize + j]
            }
            if(frame.maxOrNull()!! < 1000) continue
            diffFunc(frame, 0)
            cmndf()
            val firstMinIndex = findFirstLocalMinIndex()
            if (firstMinIndex < 1){
                continue
            }
            val frequencyPeak = 44100.0 / parabolicInterpolation(firstMinIndex)
            freqs.add(frequencyPeak)
            counter++
        }

        if (freqs.size < 1) {
            return 0.0
        }
        var retFreq = 0.0
        averageFreq = freqs.average()
        if(freqs.size > 2) {
            val temp = freqs.filter { (abs(it - averageFreq) / averageFreq < 0.5) }
            retFreq = temp.average()
        }else {
            retFreq = averageFreq
        }
        return retFreq
    }
    
    /**
     * @brief Рассчитать разностную функцию
     */
    private fun diffFunc(signal : ShortArray, startIndex: Int){
        for (tau in diffFuncData.indices) {
            var sum = 0.0
            for (j in startIndex until startIndex + windowSize){
                val difference = (signal[j] - signal[j + tau])
                sum += (difference * difference).toDouble()
            }
            diffFuncData[tau] = sum
        }
    }

    /**
     * @brief Cumulative mean normalized difference function
     */
    private fun cmndf() {
        var sum: Double = 0.0
        for (tau in 1 until cmndfData.size) {
            sum += diffFuncData[tau]
            cmndfData[tau] = tau * diffFuncData[tau] / sum
        }
    }

    private fun findFirstLocalMinIndex(): Int {
        for (i in 1..cmndfData.size - 2) {
            if (cmndfData[i] < 0.2) {
                if ((cmndfData[i] < cmndfData[i - 1]) && (cmndfData[i] < cmndfData[i+1])){
                    return i
                }
            }
        }
        return 0
    }

    private fun parabolicInterpolation(index: Int): Double {
        val alpha = cmndfData[index - 1]
        val beta = cmndfData[index]
        val gamma = cmndfData[index + 1]
        val peak = 0.5 * (alpha - gamma) / (alpha - 2 * beta + gamma)

        return index.toDouble() + peak
    }

    fun getRequiredSamplesCount(): Int {
        return frameSize * 2
    }

    private fun cutUnvoicedRegion(signal : ShortArray): Array<Int> {
        val bounds = arrayOf(0, signal.size)

        val absSignal = signal.map { abs(it.toInt()) }

        val stepCount = absSignal.size / maxLag

        var average = 0

        // нижняя граница
        for (i in 0 until stepCount) {
            for (j in maxLag * i until maxLag * (i + 1))
                average += absSignal[j]
            average /= maxLag

            if (average >= avgSignalThreshold) {
                bounds[0] = i * maxLag
                break
            }
        }

        // Верхняя граница
        for (i in stepCount - 1 downTo  0) {
            for (j in maxLag * i until maxLag * (i + 1))
                average += absSignal[j]
            average /= maxLag

            if (average >= avgSignalThreshold) {
                bounds[1] = i * maxLag
                break
            }
        }

        return bounds
    }
}