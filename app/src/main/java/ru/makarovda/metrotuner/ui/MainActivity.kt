package ru.makarovda.metrotuner.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.makarovda.metrotuner.R

class MainActivity : AppCompatActivity() {

    private val metronomeStateSettings = "MetronomeSettings"
    private val metronomeBpmStr = "BPM"
    private val metronomeBeatsStr = "Beats"
    private val metronomeStateViewModel: MetronomeStateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) { // Первое включение приложения
            getSharedPreferences(metronomeStateSettings, MODE_PRIVATE).apply {
                metronomeStateViewModel.setBpmValue(getInt(metronomeBpmStr, 120))
                getString(metronomeBeatsStr, "Xxxx")?.let {
                    metronomeStateViewModel.beatsFromString(
                        it
                    )
                }
            }
        }

        setContentView(R.layout.activity_main)
    }

    override fun onStop() {
        super.onStop()
        // Сохранение настроек в постоянную память
        getSharedPreferences(metronomeStateSettings, MODE_PRIVATE).edit().apply {
            putInt(metronomeBpmStr, metronomeStateViewModel.bpmFlow.value)
            putString(metronomeBeatsStr, metronomeStateViewModel.accentStr)
            apply()
        }
    }
}