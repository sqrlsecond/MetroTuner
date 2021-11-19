package com.example.metrotuner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MetronomeSettingsFragment: Fragment() {

    private val stateVm: MetronomeStateViewModel by activityViewModels()
    private val profilesVm: MetronomeSettingsVM by viewModels {
        MetronomeSettingsVMFactory((requireActivity().application as MetroTunerApp).repository!!)
    }

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

        //Подтверждение новых настроек
        view.findViewById<Button>(R.id.settings_confirm_btn).setOnClickListener {

            stateVm.bpm = bpmEdit.text.toString().toInt()
            stateVm.beats = beatsEdit.text.toString().toInt()
            stateVm.divider = dividerEdit.text.toString().toInt()

            findNavController().popBackStack()
        }

        // Список сохранённых профилей
        val recView = view.findViewById<RecyclerView>(R.id.presets_list)
        val adapterPresets = PresetsListAdapter()
        GlobalScope.launch(Dispatchers.Main) {
            profilesVm.profilesList.collect {
                adapterPresets.submitList(it)
            }
        }
        recView.adapter = adapterPresets

        view.findViewById<Button>(R.id.save_preset_btn).setOnClickListener {
            EnterPresetNameDialog().show(parentFragmentManager, "save Dialog")
        }
    }
}