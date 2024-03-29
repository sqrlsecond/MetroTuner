package ru.makarovda.metrotuner.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.makarovda.metrotuner.R
import ru.makarovda.metrotuner.tempos.Tempos

class TemposFragment: Fragment() {

    private val stateVm: MetronomeStateViewModel by activityViewModels()

    private val temposUiMap = HashMap<String, Float>(Tempos.temposCount)

    init {
        Tempos.tempos.forEach {
            temposUiMap[it.name] = (it.max + it.min).toFloat() / 2
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_tempos,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val slider = view.findViewById<VerticalSlider>(R.id.verticalSlider)

        slider?.initializeConverter(1.0f, 250.0f)

        slider?.labels = temposUiMap
        val bpmTV = view.findViewById<TextView>(R.id.tempo_bpm_text_view)
        bpmTV?.text = getString(R.string.tempo_bpm_text, stateVm.bpmFlow.value)

        slider?.valueChangeHandler = {
            bpmTV?.text = getString(R.string.tempo_bpm_text, it.toInt())
        }
        var sliderInitPos = stateVm.bpmFlow.value.toFloat()
            //if (sliderInitPos > 200.0f) sliderInitPos = 200.0f
        slider?.setSliderPosition(sliderInitPos)

        view.findViewById<Toolbar>(R.id.tempos_toolbar).setNavigationOnClickListener {
            stateVm.setBpmValue(slider?.getSliderPos()?.toInt() ?: stateVm.bpmFlow.value)
            findNavController().popBackStack()
        }

        view.findViewById<Button>(R.id.tempo_bpm_confirm_button).setOnClickListener{
            stateVm.setBpmValue(slider?.getSliderPos()?.toInt() ?: stateVm.bpmFlow.value)
            findNavController().popBackStack()
        }

    }
}