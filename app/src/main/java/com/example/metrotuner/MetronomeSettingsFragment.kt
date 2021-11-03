package com.example.metrotuner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

class MetronomeSettingsFragment: Fragment() {

    private val stateVm: MetronomeStateViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_metronome_settings,
                                container,
                                false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bpmEdit = view.findViewById<EditText>(R.id.bpm_edit)
        val beatsEdit = view.findViewById<EditText>(R.id.beats_count_edit)
        val dividerEdit = view.findViewById<EditText>(R.id.divider_edit)

        bpmEdit.setText(stateVm.bpm.toString())
        beatsEdit.setText(stateVm.beats.toString())
        dividerEdit.setText(stateVm.divider.toString())

        view.findViewById<Button>(R.id.settings_confirm_btn).setOnClickListener {

            stateVm.bpm = bpmEdit.text.toString().toInt()
            stateVm.beats = beatsEdit.text.toString().toInt()
            stateVm.divider = dividerEdit.text.toString().toInt()

            findNavController().popBackStack()

        }
    }
}