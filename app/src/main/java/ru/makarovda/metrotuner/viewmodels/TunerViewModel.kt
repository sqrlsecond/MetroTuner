package ru.makarovda.metrotuner.viewmodels

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.makarovda.metrotuner.domain.tuner.FrequencyNoteConverter
import ru.makarovda.metrotuner.domain.tuner.NoteMus
import ru.makarovda.metrotuner.domain.tuner.YINPitchDetection

class TunerViewModel: ViewModel() {

    private val _noteLD = MutableLiveData<NoteMus>(FrequencyNoteConverter.convert(0.0))
    val noteLD: LiveData<NoteMus>
        get() = _noteLD

    private var audioRecord: AudioRecord? = null

    private var tunerJob: Job? = null

    fun activateTuner(): Boolean {

        val pitchDetector = YINPitchDetection()

        val sampleRate = 44100
        val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO
        val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
        //Требуемое для создания AudioRecord количество байт в буфере
        val minInternalBufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            channelConfig, audioFormat)
        val yinRequiredSamplesCount = pitchDetector.getRequiredSamplesCount() * 3
        val internalBufferSize =
            if (yinRequiredSamplesCount > minInternalBufferSize / 2) yinRequiredSamplesCount
            else (minInternalBufferSize / 2)
        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                internalBufferSize * 2
            )
        } catch (e: SecurityException) {
            return false
        } catch (e: IllegalArgumentException) {
            return false
        }

        val audioBuffer = ShortArray(internalBufferSize)

        tunerJob = viewModelScope.launch {
            while(true) {
                audioRecord?.read(audioBuffer, 0, audioBuffer.size, AudioRecord.READ_BLOCKING)
                val frequency = pitchDetector.detectFrequency(audioBuffer)
                withContext(Dispatchers.Main) {
                    if(frequency >= 50.0) {
                        _noteLD.value = FrequencyNoteConverter.convert(frequency)
                    }
                }
                delay(500)
            }
        }

        return true
    }

    fun deactivateTuner() {
        audioRecord?.release()
        tunerJob?.cancel()
    }

}