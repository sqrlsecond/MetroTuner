package ru.makarovda.metrotuner.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.makarovda.metrotuner.*
import ru.makarovda.metrotuner.data.*

class MetronomeSettingsFragment: Fragment(), EnterPresetNameDialog.ResultListener, IBeatsObserver {

    private val stateVm: MetronomeStateViewModel by activityViewModels()
    /*private val profilesVm: MetronomeSettingsVM by viewModels {
        MetronomeSettingsVMFactory((requireActivity().application as MetroTunerApp).repository!!)
    }*/

    private var bpmEdit: EditText? = null
    private var beatsTextView: TextView? = null
    private var dividerEdit: EditText?  = null
    private var accentsAdapter: AccentsItemsAdapter? = null

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

        // Вывод и изменение акцентов на долях
        accentsAdapter = AccentsItemsAdapter(stateVm.accents, nonAccentColor = ResourcesCompat.getColor(resources, R.color.blue_800, null),
                                                accentColor = ResourcesCompat.getColor(resources, R.color.red, null),
                                                stateVm::changeAccent)
        view.findViewById<RecyclerView>(R.id.accents_list).apply {
            adapter = accentsAdapter
            layoutManager = GridLayoutManager(context, 4)
            //layoutManager = LinearLayoutManager(context)
        }


         bpmEdit = view.findViewById<EditText>(R.id.bpm_edit)
         beatsTextView = view.findViewById<TextView>(R.id.beats_count_text)


        //bpmEdit?.setText(stateVm.bpm.toString())
        bpmEdit?.setText(stateVm.bpmFlow.value.toString())
        beatsTextView?.text = stateVm.beats.toString()


        //Подтверждение новых настроек
        view.findViewById<Button>(R.id.settings_confirm_btn).setOnClickListener {

            //stateVm.bpm = bpmEdit?.text.toString().toInt()
            val bpmString = bpmEdit?.text.toString()
            if((TextUtils.isDigitsOnly(bpmString)) && !(TextUtils.isEmpty(bpmString))){
                stateVm.setBpmValue(bpmString.toInt())
            }
            /*val beatsString = beatsEdit?.text.toString()
            if((TextUtils.isDigitsOnly(beatsString)) && !(TextUtils.isEmpty(beatsString))){
                stateVm.beats = beatsString.toInt()
            }*/

            findNavController().popBackStack()
        }

        // Список сохранённых профилей
        /*val recView = view.findViewById<RecyclerView>(R.id.accents_list)
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

        recView.layoutManager = LinearLayoutManager(requireContext())*/

        view.findViewById<Button>(R.id.save_preset_btn).setOnClickListener {
            EnterPresetNameDialog().apply {
                setResultListener(this@MetronomeSettingsFragment)
            }.show(parentFragmentManager, "save Dialog")
        }

        view.findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            findNavController().popBackStack()
        }

       //Увеличение числа долей в такте
        view.findViewById<Button>(R.id.plus_beat_btn).setOnClickListener(){
            stateVm.changeBeats(MetronomeStateViewModel.DeltaBeats.PLUS_ONE)
        }

        //Уменьшение числа долей в такте
        view.findViewById<Button>(R.id.minus_beat_btn).setOnClickListener(){
            stateVm.changeBeats(MetronomeStateViewModel.DeltaBeats.MINUS_ONE)
        }

        //Реакция на изменение числа долей в такте
        stateVm.addBeatsObserver(this)
    }


    override fun result(name: String) {
        if (name == "null"){
            return
        }

        stateVm.bpm = bpmEdit?.text.toString().toInt()
        //stateVm.beats = beatsEdit?.text.toString().toInt()

        /*val entity = MetronomeSettingsEntity(
            name,
            stateVm.bpm,
            stateVm.beats,
            stateVm.accentStr,
        )*/
        //profilesVm.addProfile(entity)
    }

    private fun clickListener(entity: MetronomeSettingsEntity, action: PresetsListAdapter.Actions){

        when(action){
            PresetsListAdapter.Actions.CHOOSE -> {
                stateVm.bpm = entity.bpm
                //stateVm.beats = entity.beats
                //stateVm.accentStr = entity.accent

                findNavController().popBackStack()
            }
            PresetsListAdapter.Actions.DELETE -> {
                //profilesVm.deleteProfile(entity)
            }
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        stateVm.removeBeatsObserver(this)
        bpmEdit = null
        beatsTextView = null
        dividerEdit = null
    }

    override fun notifyBeatsObserver(beats: List<Boolean>) {
        beatsTextView?.text = stateVm.beats.toString()
        accentsAdapter?.accentsPattern = beats
        accentsAdapter?.notifyDataSetChanged()
    }
}