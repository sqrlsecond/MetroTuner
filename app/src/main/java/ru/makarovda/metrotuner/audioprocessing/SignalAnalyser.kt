package ru.makarovda.metrotuner.audioprocessing

import kotlin.math.sin

class SignalAnalyser {

    private val elemCount: Int = 8
    private var signalPart: Array<Float> = Array(elemCount) { 0.0f }

    init{
        val step: Float = 1.0f / elemCount
        for (i in 0..elemCount) {
            signalPart[i] = sin((i*step).toFloat())
        }
    }

}