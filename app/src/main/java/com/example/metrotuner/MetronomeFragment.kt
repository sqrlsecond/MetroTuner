package com.example.metrotuner

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MetronomeFragment(): Fragment() {

    private var counterState: Boolean = false
    private var counter: Int = 0
    private var metronomeStateText: TextView? = null
    private val stateVm: MetronomeStateViewModel by activityViewModels()
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_metronome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayer = MediaPlayer.create(context, R.raw.tick)

        // Отображение текущих параметров
        view.findViewById<TextView>(R.id.bpm_text).text = getString(R.string.bpm_text, stateVm.bpm)
        view.findViewById<TextView>(R.id.beats_text).text = getString(R.string.beats_text, stateVm.beats, stateVm.divider)

        // Запуск/остановка метронома
        view.findViewById<Button>(R.id.metronome_start_pause_btn).setOnClickListener {
            counterState = !counterState
            if(counterState){
                counterLoop()
            }
        }

        metronomeStateText = view.findViewById(R.id.metronome_state_text)

        view.findViewById<LinearLayout>(R.id.metronome_settings).setOnClickListener {
            findNavController().navigate(R.id.metronomeSettingsFragment)
        }


    }

    private fun counterLoop() {
        val pauseMs: Long = 60_000 / (stateVm.bpm.toLong())
        val countMax: Int = stateVm.beats
        GlobalScope.launch(Dispatchers.Default){
            while (counterState){
                // Обнуление счётчика долей
                if(counter >= countMax) counter = 0
                counter++
                // Отображение текущей доли
                GlobalScope.launch(Dispatchers.Main) {
                    mediaPlayer?.start()
                    metronomeStateText?.text = counter.toString()
                }
                delay(pauseMs)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        metronomeStateText?.text = "0"
        counterState = false
    }

}