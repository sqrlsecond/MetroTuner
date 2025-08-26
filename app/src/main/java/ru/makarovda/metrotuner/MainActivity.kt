package ru.makarovda.metrotuner

import ru.makarovda.metrotuner.domain.tuner.FrequencyNoteConverter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import ru.makarovda.metrotuner.viewmodels.MetronomeStateViewModel
import ru.makarovda.metrotuner.domain.metronome.MetronomeBeats

class MainActivity : AppCompatActivity() {

    private val metronomeStateSettings = "MetronomeSettings"
    private val metronomeBpmStr = "BPM"
    private val metronomeBeatsStr = "Beats"
    private val metronomeStateViewModel: MetronomeStateViewModel by viewModels()

    private val tunerA4FreqStr = "A4Freq"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (savedInstanceState == null) { // Первое включение приложения
            getSharedPreferences(metronomeStateSettings, MODE_PRIVATE).apply {
                val bpm = getInt(metronomeBpmStr, 120)
                val beatsStr = getString(metronomeBeatsStr, "Xxxx")
                metronomeStateViewModel.setSettings(bpm, beatsStr ?: "Xxxx")
                FrequencyNoteConverter.laFreq = getInt(tunerA4FreqStr, 440)
            }
        }
        setContentView(R.layout.activity_main)

    }

    override fun onStop() {
        super.onStop()
        // Сохранение настроек в постоянную память
        getSharedPreferences(metronomeStateSettings, MODE_PRIVATE).edit().apply {
            putInt(metronomeBpmStr, metronomeStateViewModel.metronomeStateLD.value!!.bpm)
            putString(
                metronomeBeatsStr,
                MetronomeBeats.convertListToString(
                    metronomeStateViewModel.metronomeStateLD.value!!.beats
                ))
            putInt(tunerA4FreqStr, FrequencyNoteConverter.laFreq)
            apply()
        }
    }
}