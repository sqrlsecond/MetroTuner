package com.example.metrotuner

import androidx.lifecycle.ViewModel

class MetronomeStateViewModel: ViewModel() {

    var bpm: Int = 120
    var beats: Int = 4
    var divider: Int = 4
}