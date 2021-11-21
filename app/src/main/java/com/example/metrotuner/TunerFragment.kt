package com.example.metrotuner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TunerFragment(): Fragment() {

    private var fragLayout: View? = null

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
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf( android.Manifest.permission.RECORD_AUDIO)
            ActivityCompat.requestPermissions(requireActivity(), permissions,0)
        }
    }

    override fun onPause() {
        super.onPause()
        SpectrumAnalyzer.actionStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragLayout = null
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.Main){
            fragLayout?.findViewById<TextView>(R.id.textView).apply {
                SpectrumAnalyzer.actionOn()
                SpectrumAnalyzer.mainFrequency.collect {
                    this?.text = it.toString()
                }
            }
        }
    }

}