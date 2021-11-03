package com.example.metrotuner


import android.media.AudioFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import android.media.MediaRecorder

import android.media.AudioRecord
import android.util.Log


object SpectrumAnalyzer {
    val mainFrequency = MutableStateFlow(0)
    var actionEnable: Boolean = false
    private var _audioRecord: AudioRecord? = null

    private const val TAG = "Spectrum Analyzer Debug"

    fun actionOn() {
        actionEnable = true
        val test = GlobalScope.launch(Dispatchers.Default){
            while(actionEnable){
                mainFrequency.value++
                delay(200)
            }
        }

    }

    fun actionStop() {
        _audioRecord?.release();
        actionEnable = false
    }

    fun createAudioRecorder() {
        val sampleRate = 441000
        val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO
        val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
        val minInternalBufferSize = AudioRecord.getMinBufferSize(
                                            sampleRate,
                                            channelConfig, audioFormat
                                         )
        val internalBufferSize = (minInternalBufferSize * 1.6).toInt()
        Log.d(TAG, "minInternalBufferSize = " + minInternalBufferSize +
                ", internalBufferSize = " + internalBufferSize)

        _audioRecord =  AudioRecord(MediaRecorder.AudioSource.MIC,
                                    sampleRate,
                                    channelConfig,
                                    audioFormat,
                                    internalBufferSize)
    }
}