class YINPitchDetection() {

    private val timeStep = 1.0F / 44100.0F;

    private val windowSize : Int = (0.025 / timeStep).toInt() //integration window 25 ms

    private val maxLag = (44100.0 / 50.0).toInt(); // 50 Hz - the lowest frequency

    private var diffFuncData = DoubleArray(maxLag)

    private var cmndfData = DoubleArray(maxLag){1.0}

    /**
     * @brief Рассчёт частоты сигнала
     */
    public fun detectFrequency(signal : ShortArray): Double {

        if (signal.size < maxLag + windowSize) {
            return 0.0
        }
        diffFunc(signal)
        cmndf()
        val firstMinIndex = findFirstLocalMinIndex()
        //не удалось найти
        if (firstMinIndex < 1){
            return 0.0
        }
        val frequencyPeak = 1 / (timeStep * parabolicInterpolation(findFirstLocalMinIndex()))
        //val frequencyPeak = 1 / (timeStep * findFirstLocalMinIndex().toDouble())
        return frequencyPeak
    }
    
    /**
     * @brief Рассчитать разностную функцию
     */
    private fun diffFunc(signal : ShortArray){
        var sum = 0.0
        var difference = 0.0
        for (tau in diffFuncData.indices) {
            sum = 0.0
            difference = 0.0
            for (j in 0..windowSize){
                difference = (signal[j] - signal[j + tau]).toDouble() / 65535
                sum += difference * difference
            }
            diffFuncData[tau] = sum
        }
    }

    /**
     * @brief Cumulative mean normalized difference function
     */
    private fun cmndf() {
        var sum:Double = 0.0
        for (tau in 1..(cmndfData.size - 1)) {
            sum = 0.0
            for (j in 0..tau){ // tau max
                sum += diffFuncData[j]
            }
            cmndfData[tau] = diffFuncData[tau].toDouble() / ((1/tau.toDouble()) * sum)
        }
    }

    private fun findFirstLocalMinIndex(): Int {
        var counter = 0
        while ((cmndfData[counter] > 0.3) && (counter < cmndfData.size - 1)){//threshold
            counter++
        }
        //no minimal extremum in function
        if(counter >= cmndfData.size - 2){
            return 0
        }
        while ((cmndfData[counter] >= cmndfData[counter + 1]) && (counter < cmndfData.size - 2)){
            counter++
        }
        return counter
    }

    private fun parabolicInterpolation(index: Int): Double {
        val alpha = cmndfData[index - 1]
        val beta = cmndfData[index]
        val gamma = cmndfData[index + 1]
        val peak = 0.5 * (alpha - gamma) / (alpha - 2 * beta + gamma)

        return index.toDouble() + peak
    }

    public fun getRequiredSamplesCount(): Int {
        return maxLag + windowSize
    }
}