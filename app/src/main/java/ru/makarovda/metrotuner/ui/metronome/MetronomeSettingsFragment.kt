package ru.makarovda.metrotuner.ui.metronome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.makarovda.metrotuner.*
import ru.makarovda.metrotuner.viewmodels.MetronomeStateViewModel

class MetronomeSettingsFragment: Fragment(){

    private val stateVm: MetronomeStateViewModel by activityViewModels()
    /*private val profilesVm: MetronomeSettingsVM by viewModels {
        MetronomeSettingsVMFactory((requireActivity().application as MetroTunerApp).repository!!)
    }*/


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
        val accentsAdapter = AccentsItemsAdapter(stateVm.metronomeStateLD.value!!.beats, nonAccentColor = ResourcesCompat.getColor(resources, R.color.blue_800, null),
                                                accentColor = ResourcesCompat.getColor(resources, R.color.red, null),
                                                stateVm::changeAccent)
        view.findViewById<RecyclerView>(R.id.accents_list).apply {
            adapter = accentsAdapter
            layoutManager = GridLayoutManager(context, 4)
        }

        val beatsTextView = view.findViewById<TextView>(R.id.beats_count_text)

        //Подтверждение новых настроек
        view.findViewById<Button>(R.id.settings_confirm_btn).setOnClickListener {
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

        /*view.findViewById<Button>(R.id.save_preset_btn).setOnClickListener {
            EnterPresetNameDialog().apply {
                setResultListener(this@MetronomeSettingsFragment)
            }.show(parentFragmentManager, "save Dialog")
        }*/

        view.findViewById<Toolbar>(R.id.beats_toolbar).setNavigationOnClickListener {
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
        stateVm.metronomeStateLD.observe(viewLifecycleOwner){
            beatsTextView.text = it.beats.size.toString()
            //accentsAdapter.submitNewList(it.beats)
            accentsAdapter.accentsPattern = it.beats
            accentsAdapter.notifyDataSetChanged()
        }

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.beats_toolbar)) { view, insets ->
            val innerPadding = insets.getInsets(
                WindowInsetsCompat.Type.statusBars() or
                        WindowInsetsCompat.Type.displayCutout()
            )
            view.setPadding(
                innerPadding.left,
                innerPadding.top,
                innerPadding.right,
                innerPadding.bottom
            )

            insets
        }
    }
}