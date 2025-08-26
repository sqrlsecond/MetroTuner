package ru.makarovda.metrotuner.ui.metronome


import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.makarovda.metrotuner.R
import ru.makarovda.metrotuner.domain.metronome.Tempos
import ru.makarovda.metrotuner.viewmodels.MetronomeStateViewModel


class MetronomeFragment: Fragment(), SetBpmDialogResultListener {

    private var counterState: Boolean = false
    private var metronomeStateText: TextView? = null
    private val stateVm: MetronomeStateViewModel by activityViewModels()
    private var playPauseBtn:ImageButton? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_metronome_snd, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Отображение текущих параметров
        val bpmTextView = view.findViewById<TextView>(R.id.mtrn_bpm_text_view)
        view.findViewById<LinearLayout>(R.id.metronome_bpm).setOnClickListener {
            val dialogSetBpm = SetBpmDialog().also {
                it.resultListener = this
            }
            dialogSetBpm.show(parentFragmentManager, "Enter BPM")
        }

        val beatsTextView: TextView = view.findViewById<TextView>(R.id.beats_text_view)

        val accentsTextView =  view.findViewById<TextView>(R.id.accent_text_view)

        val tempoTextView = view.findViewById<TextView>(R.id.tempo_text_view)

        playPauseBtn = view.findViewById<ImageButton>(R.id.mtrn_play_pause_btn)

        stateVm.metronomeStateLD.observe(viewLifecycleOwner) {
            bpmTextView.text = it.bpm.toString()
            tempoTextView.text = Html.fromHtml(
                getString(
                    R.string.metronome_tempo_text,
                    Tempos.getTempoName(it.bpm)
                ), FROM_HTML_MODE_LEGACY
            )
            beatsTextView.text = Html.fromHtml(getString(R.string.beats_text, it.beats.size), FROM_HTML_MODE_LEGACY)
            val accentsArr = ArrayList<String>(it.beats.size)
            it.beats.forEachIndexed { index, b -> if(b) accentsArr.add((index+1).toString())}
            accentsTextView.text = Html.fromHtml(getString(R.string.accent_text, accentsArr.joinToString(), FROM_HTML_MODE_LEGACY))

            if(it.active) { // Метроном сейчас работает
                playPauseBtn?.setImageResource(R.drawable.ic_baseline_pause_circle_filled_64)
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                playPauseBtn?.setImageResource(R.drawable.ic_baseline_play_circle_filled_64)
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        // Запуск/остановка метронома
        playPauseBtn!!.setOnClickListener {
            stateVm.togglePlayState()
        }

        metronomeStateText = view.findViewById(R.id.metronome_state_text_view)
        stateVm.currentBeat.observe(viewLifecycleOwner) {
            metronomeStateText!!.text = it.toString()
        }

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

        view.findViewById<TextView>(R.id.tempo_text_view).setOnClickListener {
            findNavController().navigate(R.id.temposFragment)
        }

    }

    override fun onPause() {
        super.onPause()
        stateVm.stopMetronome()
        metronomeStateText?.text = "1"
    }

    override fun onResume() {
        super.onResume()
        //playPauseBtn?.setImageResource(R.drawable.ic_baseline_play_circle_filled_64)
    }

    override fun onResult(bpm: Int) {
        stateVm.setBpmValue(bpm)
    }

}
