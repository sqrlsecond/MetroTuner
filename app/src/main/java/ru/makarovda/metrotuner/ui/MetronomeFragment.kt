package ru.makarovda.metrotuner.ui

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.makarovda.metrotuner.R
import java.util.*
import kotlin.collections.ArrayList


class MetronomeFragment: Fragment(), SetBpmDialogResultListener {

    private var counterState: Boolean = false
    //private var counter: Int = 0
    private var metronomeStateText: TextView? = null
    private val stateVm: MetronomeStateViewModel by activityViewModels()
    private var mediaPlayer: MediaPlayer? = null
    private var mediaPlayerAccent: MediaPlayer? = null
    private var playPauseBtn:ImageButton? = null
    private var pauseMs: Long = 60_000 / (120.toLong())
    private var timer: Timer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("STARTTIME", System.nanoTime().toString())
        return inflater.inflate(R.layout.fragment_metronome_snd, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaPlayer = MediaPlayer.create(context, R.raw.click)
        mediaPlayerAccent = MediaPlayer.create(context, R.raw.accent)
        Log.d("STARTTIME", System.nanoTime().toString())
        // Отображение текущих параметров
        val bpmTextView = view.findViewById<TextView>(R.id.mtrn_bpm_text_view)
        bpmTextView.text = stateVm.bpmFlow.value.toString()
        view.findViewById<LinearLayout>(R.id.metronome_bpm).setOnClickListener {
            //findNavController().navigate(R.id.metronomeSettingsFragment)
            val dialogSetBpm = SetBpmDialog().also {
                it.resultListener = this
            }
            dialogSetBpm.show(parentFragmentManager, "Enter BPM")
        }
        view.findViewById<TextView>(R.id.beats_text_view).text = getString(R.string.beats_text, stateVm.beats)
        // Отображение сильных долей в такте
        val accentsArr = ArrayList<String>()
        for ((index, value) in stateVm.accents.iterator().withIndex()){
            if (value) {
                accentsArr.add((index + 1).toString())
            }
        }
        view.findViewById<TextView>(R.id.accent_text_view).text = getString(R.string.accent_text, accentsArr.joinToString())


        lifecycleScope.launch(Dispatchers.Main){
            stateVm.bpmFlow.collect {
                pauseMs = 60_000 / (it.toLong())
                bpmTextView.text = it.toString()
                timer?.let {
                    if(counterState) {
                        timer?.cancel()
                        timer = Timer()
                        timer?.scheduleAtFixedRate(MetronomeTimerTask(),0, pauseMs)
                    }
                }
            }
        }

        // Запуск/остановка метронома
        playPauseBtn = view.findViewById<ImageButton>(R.id.mtrn_play_pause_btn)
        playPauseBtn?.setOnClickListener {
            counterState = !counterState
            if(counterState){
                playPauseBtn?.setImageResource(R.drawable.ic_baseline_pause_circle_filled_64)
                //mediaPlayer = MediaPlayer.create(context, R.raw.click)
                //mediaPlayerAccent = MediaPlayer.create(context, R.raw.accent)

                timer = Timer()
                timer?.scheduleAtFixedRate(MetronomeTimerTask(),0, pauseMs)
            } else {
                playPauseBtn?.setImageResource(R.drawable.ic_baseline_play_circle_filled_64)
                timer?.cancel()
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

        view.findViewById<Button>(R.id.tap_tempo_button).setOnClickListener {
            stateVm.tempClickHandler()
        }
    }

    override fun onPause() {
        super.onPause()

        metronomeStateText?.text = "0"
        counterState = false
        timer?.cancel()
        timer = null
        mediaPlayer?.release()
        mediaPlayer = null
        mediaPlayerAccent?.release()
        mediaPlayerAccent = null
    }

    override fun onResume() {
        super.onResume()
        playPauseBtn?.setImageResource(R.drawable.ic_baseline_play_circle_filled_64)
    }

    override fun onResult(bpm: Int) {
        stateVm.setBpmValue(bpm)
    }

    private inner class MetronomeTimerTask(): TimerTask()
    {
        private val beats = stateVm.accents
        private val countMax: Int = beats.size
        private var counter = 0

        override fun run() {
            if(counter >= countMax) counter = 0
            counter++
            Log.d("METRONOME", System.currentTimeMillis().toString())
            if(beats[counter-1]) {//Сильная доля
                mediaPlayerAccent?.start()
            } else {//Слабая доля
                mediaPlayer?.start()
            }
            lifecycleScope.launch(Dispatchers.Main) {
                metronomeStateText?.text = counter.toString()
            }
        }
    }
}
