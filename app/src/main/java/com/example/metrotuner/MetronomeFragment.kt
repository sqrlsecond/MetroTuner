package com.example.metrotuner

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MetronomeFragment(): Fragment() {

    private var counterState: Boolean = false
    private var counter: Int = 0
    private var metronomeStateText: TextView? = null
    private val stateVm: MetronomeStateViewModel by activityViewModels()
    private var mediaPlayer: MediaPlayer? = null
    private var mediaPlayerAccent: MediaPlayer? = null
    private var playPauseBtn:ImageButton? = null
    private var pauseMs: Long = 60_000 / (120.toLong())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_metronome_snd, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayer = MediaPlayer.create(context, R.raw.click)
        mediaPlayerAccent = MediaPlayer.create(context, R.raw.accent)

        // Отображение текущих параметров
        val bpmTextView = view.findViewById<TextView>(R.id.mtrn_bpm_text_view)
        bpmTextView.text = stateVm.bpmFlow.value.toString()
        bpmTextView.setOnClickListener {
            findNavController().navigate(R.id.metronomeSettingsFragment)
        }
        view.findViewById<TextView>(R.id.beats_text_view).text = getString(R.string.beats_text, stateVm.beats)
        view.findViewById<TextView>(R.id.accent_text_view).text = getString(R.string.accent_text, stateVm.accent)

        lifecycleScope.launch(Dispatchers.Main){
            stateVm.bpmFlow.collect {
                pauseMs = 60_000 / (it.toLong())
                bpmTextView.text = it.toString()
            }
        }
        // Запуск/остановка метронома
        playPauseBtn = view.findViewById<ImageButton>(R.id.mtrn_play_pause_btn)
        playPauseBtn?.setOnClickListener {
            counterState = !counterState
            if(counterState){
                playPauseBtn?.setImageResource(R.drawable.ic_baseline_pause_circle_filled_64)
                counterLoop()
            } else {
                playPauseBtn?.setImageResource(R.drawable.ic_baseline_play_circle_filled_64)
            }
        }

        metronomeStateText = view.findViewById(R.id.metronome_state_text_view)

        view.findViewById<LinearLayout>(R.id.beats_settings_linear_layout).setOnClickListener {
            findNavController().navigate(R.id.metronomeSettingsFragment)
        }

        view.findViewById<Button>(R.id.pls_1_bpm_btn).setOnClickListener {
            stateVm.changeBpm(1)
        }

        view.findViewById<Button>(R.id.pls_5_bpm_btn).setOnClickListener {
            stateVm.changeBpm(5)
        }

        view.findViewById<Button>(R.id.min_1_bpm_btn).setOnClickListener {
            stateVm.changeBpm(-1)
        }

        view.findViewById<Button>(R.id.min_5_bpm_btn).setOnClickListener {
            stateVm.changeBpm(-5)
        }

    }

    private fun counterLoop() {
        counter = 0
        val countMax: Int = stateVm.beats
        lifecycleScope.launch(Dispatchers.Default){
            while (counterState){
                // Обнуление счётчика долей
                if(counter >= countMax) counter = 0
                counter++
                if(counter == 1) {
                    mediaPlayerAccent?.start()
                }else {
                    mediaPlayer?.start()
                }
                // Отображение текущей доли
                lifecycleScope.launch(Dispatchers.Main) {
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

    override fun onResume() {
        super.onResume()
        playPauseBtn?.setImageResource(R.drawable.ic_baseline_play_circle_filled_64)
    }

}