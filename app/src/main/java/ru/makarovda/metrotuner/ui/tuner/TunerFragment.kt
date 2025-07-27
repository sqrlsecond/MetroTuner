package ru.makarovda.metrotuner.ui.tuner

import ru.makarovda.metrotuner.domain.tuner.FrequencyNoteConverter
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.makarovda.metrotuner.R
import ru.makarovda.metrotuner.viewmodels.TunerViewModel

class TunerFragment: Fragment() {

    private val tunerVM: TunerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tuner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)
            ActivityCompat.requestPermissions(requireActivity(), permissions,0)
        }

        val freqA4TextView = view.findViewById<TextView>(R.id.a4_freq_textView)
        freqA4TextView.text = getString(R.string.a4_value_text, FrequencyNoteConverter.laFreq)

        view.findViewById<Button>(R.id.freq_inc_btn)
            .setOnClickListener{
                FrequencyNoteConverter.laFreq++
                freqA4TextView.text = getString(R.string.a4_value_text, FrequencyNoteConverter.laFreq)
            }

        view.findViewById<Button>(R.id.freq_dec_btn)
            .setOnClickListener{
                FrequencyNoteConverter.laFreq--
                freqA4TextView.text = getString(R.string.a4_value_text, FrequencyNoteConverter.laFreq)
            }

        val noteText = view.findViewById<TextView>(R.id.noteTextView)
        val centsText = view.findViewById<TextView>(R.id.centsTextView)
        val pitchMeter = view.findViewById<HorizontalPitchMeter>(R.id.horizontalPitchMeter)

        tunerVM.noteLD.observe(this) {note ->
            noteText.text = note.nearestNote
            centsText.text = note.cents.toString()
            pitchMeter.setDeviation(note.cents)
        }
    }

    override fun onPause() {
        super.onPause()
        tunerVM.deactivateTuner()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }


    override fun onResume() {
        super.onResume()
        if(tunerVM.activateTuner()) {
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}