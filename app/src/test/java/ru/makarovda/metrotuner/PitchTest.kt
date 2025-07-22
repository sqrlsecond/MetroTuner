package ru.makarovda.metrotuner

import ru.makarovda.metrotuner.domain.tuner.YINPitchDetection
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal
import kotlin.math.sin

class PitchTest {



    @Test
    fun pitchTest() {

        val detector = YINPitchDetection()

        val freqs = readTargetFrequencies()


        freqs.forEach {(key, targetFrequency) ->
            val inputStream = this.javaClass.classLoader?.getResourceAsStream(key)

            inputStream?.let {

                val bsArr = ByteArray(5)

                inputStream.skip(40)
                inputStream.read(bsArr, 0, 4)

                var signalLength = 0
                for (i in 0..3) {
                    signalLength += bsArr[i].toInt() shl (i * 8)
                }

                signalLength /= 2

                val audioSignal = ShortArray(signalLength)

                for (i in 0 until signalLength) {
                    audioSignal[i] = ((inputStream.read() or (inputStream.read() shl 8))).toShort()
                }

                val calculatedFreq = detector.detectFrequency(audioSignal) * 2
                println(calculatedFreq)
                assertEquals(targetFrequency, calculatedFreq.toFloat(), 0.005f * targetFrequency)
            }

        }
    }

    private fun readTargetFrequencies(): Map<String, Float> {

        val inputStream = this.javaClass.classLoader?.getResourceAsStream("notes.json")


        val streamReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        val responseStrBuilder = StringBuilder()

        var inputStr: String?
        while (streamReader.readLine().also { inputStr = it } != null) responseStrBuilder.append(
            inputStr
        )

        val jsonObject = JSONObject(responseStrBuilder.toString())

        val targetFreqs = HashMap<String, Float>()

        for (name in jsonObject.keys()) {
            targetFreqs[name] = (jsonObject[name] as BigDecimal).toFloat()
        }
        //print(targetFreqs)
        return targetFreqs
    }

    @Test
    fun sineTest(){

        val detector = YINPitchDetection()

        val arrSize = detector.getRequiredSamplesCount() * 2

        val audioSignal = ShortArray(arrSize)

        val freq = 500.0

        val timeStep = 1.0 / 44100.0

        for (i in 0 until arrSize) {
            audioSignal[i] = (sin(2 * kotlin.math.PI * freq * timeStep * i) * 16384).toInt().toShort()
        }


        val calculatedFreq = detector.detectFrequency(audioSignal)
        print(calculatedFreq)

        assertEquals(freq, calculatedFreq, 0.004 * freq)

    }

}