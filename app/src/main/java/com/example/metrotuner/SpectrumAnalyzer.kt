package com.example.metrotuner

import myComplex.Complex
import android.media.AudioFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import android.media.MediaRecorder

import android.media.AudioRecord
import android.util.Log
import kotlinx.coroutines.flow.StateFlow
import java.lang.Math.pow
import kotlin.math.*

/**
 * Класс для анализа спектра входящего аудиосигнала
 */

object SpectrumAnalyzer {
    /**
     * Частота с наибольшей амплитудой в сигнале
     */
    private val _mainFrequency = MutableStateFlow(0)

    /**
     * StateFlow для получения значения частоты с наибольшей амплитудой в сигнале
     */
    val mainFrequency: StateFlow<Int>
        get(){return _mainFrequency}

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
     * Буфер для хранения данных для преобразования Фурье
     * до вызова fft() должен хранить дискретные отчёты сигнала
     * после вызова хранит комплексные значения частот
     */
    private var _signalProcessingBuffer: Array<Complex>? = null

    /**
     * Тег для вывода сообщений в лог
     */
    private const val TAG = "Spectrum Analyzer Debug"

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
        var frequency: Int = 0
        GlobalScope.launch(Dispatchers.Default){
            _audioRecord?.startRecording();
            while(actionEnable){
                _audioRecord?.read(_audioBuffer!!, 0, bufSize!!)
                fft()
                 frequency = findMaxAmplFreq(8000).toInt()
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
     * Создание и инициализация объекта для работы с аудио
     */
    private fun createAudioRecorder() {
        val sampleRate = 8000
        val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO
        val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
        //Требуемое для создания AudioRecord количество байт в буфере
        val minInternalBufferSize = AudioRecord.getMinBufferSize(
                                            sampleRate,
                                            channelConfig, audioFormat)
        // Количество элементов в буфере для хранения данных с микрофона
        // Буфер для работы алгоритма БПФ должен быть выражен 2 в целой степени
        // (128, 256, 512 и т.д.)
        val requiredPow = (ceil(log2(minInternalBufferSize.toDouble()))).toInt() - 1
        val internalBufferSize = 2.0.pow(requiredPow.toDouble()).toInt()

        _audioBuffer = ShortArray(internalBufferSize)
        _signalProcessingBuffer = Array<Complex>(internalBufferSize){Complex(0.0, 0.0)}

        Log.d(TAG, "minInternalBufferSize = " + minInternalBufferSize +
                ", internalBufferSize = " + internalBufferSize)

        _audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,
                                    sampleRate,
                                    channelConfig,
                                    audioFormat,
                                    internalBufferSize)

        val test = _audioRecord?.state
    }

    /**
     * Алгоритм быстрого преобразования Фурье
     */
    private fun fft()
    {
        if (_signalProcessingBuffer == null){
            return
        }
        val n = _signalProcessingBuffer!!.size
        for (i in _signalProcessingBuffer?.indices!!){
            _signalProcessingBuffer!![i] = Complex(_audioBuffer!![i].toDouble(), 0.0)
        }
        samplesSort()
        var len = 2
        while(len <= n) {
            val ang = - 2 * PI / len
            val wlen = Complex(cos(ang), sin(ang))
            for (i in 0 until n step len) {
                var w = Complex(1.0, 0.0)
                for (j in 0 until len / 2) {
                    val u = _signalProcessingBuffer!![i + j]
                    val v = w * _signalProcessingBuffer!![i + j + len / 2]
                    _signalProcessingBuffer!![i + j] = u + v
                    _signalProcessingBuffer!![i + j + len/2] = u - v
                    w *= wlen
                }
            }
            len = len shl (1)
        }
    }

    /**
     * Упорядочивание элементов в буфере для БПФ
     */
    private fun samplesSort()
    {
        val n = _signalProcessingBuffer!!.size
        var j = 0
        var temp = Complex(0.0, 0.0)
        //Расчёт бит в обратном порядке
        for(i in 1 until n){
            var bit = n shr(1)
            while ((j and bit) != 0){
                j = j xor bit
                bit = bit shr 1
            }
            j = j xor bit

            if(i < j){
                temp = _signalProcessingBuffer!![i]
                _signalProcessingBuffer!![i] = _signalProcessingBuffer!![j]
                _signalProcessingBuffer!![j] = temp
            }
        }
    }

    /**
     * Значение частоты с максимальной амплитудой в спектре
     * @param samplingFrequency - частота семплирования исходного сигнала
     */
    fun findMaxAmplFreq(samplingFrequency: Int): Double
    {
        var maxVal = 0.0
        var frequincyStep = samplingFrequency.toDouble() / _signalProcessingBuffer!!.size
        var maxFreqIndex = 0
        // Проверка корректна только для части спектра не превышающией половины частоты дискретизации
        for (i in 0.._signalProcessingBuffer!!.size / 2){
            if (_signalProcessingBuffer!![i].module > maxVal){
                maxVal = _signalProcessingBuffer!![i].module
                maxFreqIndex = i
            }
        }
        return frequincyStep * maxFreqIndex
    }
}

