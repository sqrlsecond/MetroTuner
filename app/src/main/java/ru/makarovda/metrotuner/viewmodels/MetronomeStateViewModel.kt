package ru.makarovda.metrotuner.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.makarovda.metrotuner.R
import ru.makarovda.metrotuner.domain.metronome.MetronomeBeats
import ru.makarovda.metrotuner.ui.metronome.MetronomeState
import ru.makarovda.metrotuner.utility.EventIntervalCalculator
import java.util.*
import kotlin.math.round

/**
 *   ViewModel для описания текущего состояния
 */
class MetronomeStateViewModel(application: Application): AndroidViewModel(application) {

    enum class DeltaBeats{
        PLUS_ONE,
        MINUS_ONE
    }

    private val tempCalc = EventIntervalCalculator(10, 6000, 10) {
        setBpmValue(round(6000.0 / it.toDouble()).toInt())
    }

    private val _currentBeat = MutableLiveData<Int>(1)
    val currentBeat: LiveData<Int>
        get() = _currentBeat

    private var timer: Timer? = null

    private val metronomeTaskConfig = MetronomeTaskConfig(
        application,
        R.raw.accent,
        R.raw.click
    )
    // Изменение числа ударов в минуту
    // delta - значение, которое необходимо добавить

    private var metronomeBeats = MetronomeBeats.fromString("Xxxx")

    private val _metronomeStateLD = MutableLiveData<MetronomeState>(
        MetronomeState(
            120,
            false,
            metronomeBeats.beats
        )
    )
    val metronomeStateLD: LiveData<MetronomeState>
        get() = _metronomeStateLD

    fun changeBpm(delta: Int){
        setBpmValue(_metronomeStateLD.value!!.bpm + delta)
    }

    fun setBpmValue(newBpm: Int){
        var bpmValue = newBpm
        if(newBpm < 1){
            bpmValue = 1
        } else if(newBpm > 250){
            bpmValue = 250
        }
        _metronomeStateLD.value = _metronomeStateLD.value!!.copy(
            bpm = bpmValue,
        )
        if (_metronomeStateLD.value!!.active) startMetronome()
    }

    fun beatsFromString(beatsStr: String){
        if (beatsStr.isEmpty()) return
        metronomeBeats = MetronomeBeats.fromString(beatsStr)
        _metronomeStateLD.value = _metronomeStateLD.value!!.copy(
            beats = metronomeBeats.beats
        )
    }

    fun changeBeats(delta: DeltaBeats){
        when (delta){
            DeltaBeats.MINUS_ONE -> metronomeBeats.minusOne()
            DeltaBeats.PLUS_ONE -> metronomeBeats.plusOne()
        }
        _metronomeStateLD.value = _metronomeStateLD.value!!.copy(
            beats = metronomeBeats.beats
        )
    }

    fun tempClickHandler() {
        tempCalc.eventHandler()
    }

    fun changeAccent(index: Int) {
        metronomeBeats.changeAccent(index)
        _metronomeStateLD.value = _metronomeStateLD.value!!.copy(
            beats = metronomeBeats.beats
        )
    }

    @SuppressLint("DiscouragedApi")
    fun startMetronome() {
        timer?.cancel()
        timer = Timer().apply {
            val pauseMs = 60_000 / (_metronomeStateLD.value!!.bpm.toLong())
            scheduleAtFixedRate(MetronomeTimerTask(metronomeTaskConfig), 0, pauseMs)
        }
        _metronomeStateLD.value = _metronomeStateLD.value!!.copy(
            active = true
        )
    }

    fun stopMetronome() {
        timer?.cancel()
        _metronomeStateLD.value = _metronomeStateLD.value!!.copy(
            active = false
        )
    }

    fun togglePlayState() {
        if(_metronomeStateLD.value!!.active) {
            stopMetronome()
        } else {
            startMetronome()
        }

    }

    fun setSettings(bpm: Int, beatsStr: String) {
        metronomeBeats = MetronomeBeats.fromString(beatsStr)
        _metronomeStateLD.value = _metronomeStateLD.value!!.copy(
            bpm = bpm,
            beats = metronomeBeats.beats,
            active = false
        )
    }

    private inner class MetronomeTaskConfig(
        context: Context,
        accentSoundResId: Int,
        clickSoundResId: Int
    )
    {
        val soundPool: SoundPool
        val accentSoundId: Int
        val clickSoundId: Int

        init {
            val audioAttrib = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            soundPool = SoundPool.Builder()
                .setAudioAttributes(audioAttrib)
                .setMaxStreams(2)
                .build()
            accentSoundId = soundPool.load(context, accentSoundResId, 1)
            clickSoundId = soundPool.load(context, clickSoundResId, 2)
        }
    }

    private inner class MetronomeTimerTask(
        config: MetronomeTaskConfig
    ): TimerTask()
    {
        private val countMax: Int = metronomeBeats.size
        private var counter = 0
        private val soundPool: SoundPool = config.soundPool
        private val accentSoundId: Int = config.accentSoundId
        private val clickSoundId: Int = config.clickSoundId

        override fun run() {
            viewModelScope.launch(Dispatchers.Main) {
                if(counter >= countMax) counter = 0
                counter++
                //Log.d("METRONOME", (System.nanoTime() / 1_000_000).toString())
                if(metronomeBeats.beats[counter-1]) {//Сильная доля
                    soundPool.play(accentSoundId, 1.0f, 1.0f, 0, 0, 1.0f)
                } else {//Слабая доля
                    soundPool.play(clickSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
                }
                _currentBeat.value = counter
            }
        }
    }


}