package com.example.metrotuner

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Класс для анализа входящего аудиосигнала. Определение первой гармоники
 */

object AudioSignalProccesor {

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

        GlobalScope.launch(Dispatchers.Default){
            _audioRecord?.startRecording();
            //flag = true
            while(actionEnable){
                _audioRecord?.read(_audioBuffer!!, 0, bufSize!!, AudioRecord.READ_BLOCKING)

                frequency = YINPitchDetection.detectFrequency(_audioBuffer!!)
                if(frequency > 0) _mainFrequency.value = frequency
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
        val yinRequiredSamplesCount = YINPitchDetection.getRequiredSamplesCount()
        val internalBufferSize = if (yinRequiredSamplesCount > minInternalBufferSize / 2) yinRequiredSamplesCount else (minInternalBufferSize / 2)

        _audioBuffer = ShortArray(internalBufferSize)

        /*Log.d(
            TAG, "minInternalBufferSize = " + minInternalBufferSize +
                ", internalBufferSize = " + internalBufferSize)*/

        _audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            internalBufferSize)

        val test = _audioRecord?.state
    }
}