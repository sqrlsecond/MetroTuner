package ru.makarovda.metrotuner.ui.metronome

data class MetronomeState(
    val bpm: Int,
    val active: Boolean,
    val beats: List<Boolean>
)
