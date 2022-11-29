package ru.makarovda.metrotuner.ui

import FrequencyNoteConverter
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.makarovda.metrotuner.audioprocessing.AudioSignalProccesor
import ru.makarovda.metrotuner.R

class TunerFragment(): Fragment() {

    private var fragLayout: View? = null
    private var audioProcessor: AudioSignalProccesor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tuner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragLayout = view
        audioProcessor = AudioSignalProccesor()
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf( android.Manifest.permission.RECORD_AUDIO)
            ActivityCompat.requestPermissions(requireActivity(), permissions,0)
        }
    }

    override fun onPause() {
        super.onPause()
        audioProcessor?.actionStop()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragLayout = null
    }

    override fun onResume() {
        super.onResume()
        val noteText = fragLayout?.findViewById<TextView>(R.id.noteTextView)
        val centsText = fragLayout?.findViewById<TextView>(R.id.centsTextView)
        val pitchMeter = fragLayout?.findViewById<HorizontalPitchMeter>(R.id.horizontalPitchMeter)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        lifecycleScope.launch(Dispatchers.Main){
            audioProcessor?.actionOn()
            audioProcessor?.mainFrequency?.collect {
                val note = FrequencyNoteConverter.convert(it)
                noteText?.text = note.nearestNote
                centsText?.text = note.cents.toString()
                pitchMeter?.setDeviation(note.cents)
            }
        }
    }
}