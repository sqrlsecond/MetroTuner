package ru.makarovda.metrotuner.audioprocessing

import YINPitchDetection
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Класс для анализа входящего аудиосигнала. Определение первой гармоники
 */

class AudioSignalProccesor() {

    /**
     * Частота первой гармоники в сигнале
     */
    private val _mainFrequency = MutableStateFlow(0.0)

    /**
     * StateFlow для получения значения частоты с первой гармоники в сигнале
     */
    val mainFrequency: StateFlow<Double>
        get(){return _mainFrequency }

    /**
     * Активирован рабочий режим
     */
    var actionEnable: Boolean = false

    /**
     * Объект для получения данных с микрофона
     */
    private var _audioRecord: AudioRecord? = null

    /**
     * Буфер для хранения дискретных отчётов сигнала
     */
    private var _audioBuffer: ShortArray? = null

    private var _frequencyIntegratorBuffer = DoubleArray(10)

    private val pitchDetector: YINPitchDetection by lazy { YINPitchDetection()}

    /**
     * Активировать анализатор
     */
    fun actionOn() {
        actionEnable = true
        //Создание объекта для получения данных с микрофона
        if(_audioRecord == null){
            createAudioRecorder()
        }
        val bufSize = _audioBuffer?.size
        var frequency: Double = 0.0
        var counter = 0
        var recordingResult: Int? = 0

        GlobalScope.launch(Dispatchers.Default){
            _audioRecord?.startRecording();
            //flag = true
            while(actionEnable){
                recordingResult = _audioRecord?.read(_audioBuffer!!, 0, bufSize!!, AudioRecord.READ_BLOCKING)
                //Log.d("Recording_res", recordingResult.toString())
                frequency = pitchDetector.detectFrequency(_audioBuffer!!)

                /*if (counter >= 10) {
                    counter = 0
                    _mainFrequency.value = _frequencyIntegratorBuffer.sum()
                    continue
                }
                if(frequency >= 50.0) {
                    _frequencyIntegratorBuffer[counter] = frequency * 0.1
                    counter++
                }*/

                if(frequency >= 50.0) {
                    _mainFrequency.value = frequency
                }
                delay(500)
            }
        }
    }

    fun actionStop() {
        _audioRecord?.release();
        actionEnable = false
        _audioRecord = null
    }
    /**
     * Create and initialization object for working with audio signal
     * Создание и инициализация объекта для работы с аудио
     */
    private fun createAudioRecorder() {
        val sampleRate = 44100
        val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO
        val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
        //Требуемое для создания AudioRecord количество байт в буфере
        val minInternalBufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            channelConfig, audioFormat)
        val yinRequiredSamplesCount = pitchDetector.getRequiredSamplesCount() * 3
        val internalBufferSize = if (yinRequiredSamplesCount > minInternalBufferSize / 2) yinRequiredSamplesCount else (minInternalBufferSize / 2)


        Log.d("jopa", yinRequiredSamplesCount.toString())
        Log.d("jopa", (minInternalBufferSize / 2).toString())
        _audioBuffer = ShortArray(internalBufferSize)

        /*Log.d(
            TAG, "minInternalBufferSize = " + minInternalBufferSize +
                ", internalBufferSize = " + internalBufferSize)*/

        _audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            internalBufferSize*2)

        val test = _audioRecord?.state
    }
}