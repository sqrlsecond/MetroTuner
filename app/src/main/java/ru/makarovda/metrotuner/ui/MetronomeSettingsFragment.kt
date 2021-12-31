package ru.makarovda.metrotuner.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.makarovda.metrotuner.*
import ru.makarovda.metrotuner.data.MetronomeSettingsEntity

class MetronomeSettingsFragment: Fragment(), EnterPresetNameDialog.ResultListener {

    private val stateVm: MetronomeStateViewModel by activityViewModels()
    private val profilesVm: MetronomeSettingsVM by viewModels {
        MetronomeSettingsVMFactory((requireActivity().application as MetroTunerApp).repository!!)
    }

    private var bpmEdit: EditText? = null
    private var beatsEdit: EditText? = null
    private var dividerEdit: EditText?  = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_metronome_settings,
                                container,
                                false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         bpmEdit = view.findViewById<EditText>(R.id.bpm_edit)
         beatsEdit = view.findViewById<EditText>(R.id.beats_count_edit)


        //bpmEdit?.setText(stateVm.bpm.toString())
        bpmEdit?.setText(stateVm.bpmFlow.value.toString())
        beatsEdit?.setText(stateVm.beats.toString())


        //Подтверждение новых настроек
        view.findViewById<Button>(R.id.settings_confirm_btn).setOnClickListener {

            //stateVm.bpm = bpmEdit?.text.toString().toInt()
            val bpmString = bpmEdit?.text.toString()
            if((TextUtils.isDigitsOnly(bpmString)) && !(TextUtils.isEmpty(bpmString))){
                stateVm.setBpmValue(bpmString.toInt())
            }
            val beatsString = beatsEdit?.text.toString()
            if((TextUtils.isDigitsOnly(beatsString)) && !(TextUtils.isEmpty(beatsString))){
                stateVm.beats = beatsString.toInt()
            }



            findNavController().popBackStack()
        }

        // Список сохранённых профилей
        val recView = view.findViewById<RecyclerView>(R.id.presets_list)
        val adapterPresets = PresetsListAdapter(){ entity: MetronomeSettingsEntity,
                                                   action: PresetsListAdapter.Actions ->
                                                  clickListener(entity, action)
        }
        GlobalScope.launch(Dispatchers.Main) {
            profilesVm.profilesList.collect {
                adapterPresets.submitList(it)
            }
        }
        recView.adapter = adapterPresets

        recView.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<Button>(R.id.save_preset_btn).setOnClickListener {
            EnterPresetNameDialog().apply {
                setResultListener(this@MetronomeSettingsFragment)
            }.show(parentFragmentManager, "save Dialog")
        }
    }


    override fun result(name: String) {
        if (name == "null"){
            return
        }

        stateVm.bpm = bpmEdit?.text.toString().toInt()
        stateVm.beats = beatsEdit?.text.toString().toInt()

        val entity = MetronomeSettingsEntity(
            name,
            stateVm.bpm,
            stateVm.beats,
            stateVm.accent,
        )
        profilesVm.addProfile(entity)
    }

    private fun clickListener(entity: MetronomeSettingsEntity, action: PresetsListAdapter.Actions){

        when(action){
            PresetsListAdapter.Actions.CHOOSE -> {
                stateVm.bpm = entity.bpm
                stateVm.beats = entity.beats
                stateVm.accent = entity.accent

                findNavController().popBackStack()
            }
            PresetsListAdapter.Actions.DELETE -> {
                profilesVm.deleteProfile(entity)
            }
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        bpmEdit = null
        beatsEdit = null
        dividerEdit = null
    }
}