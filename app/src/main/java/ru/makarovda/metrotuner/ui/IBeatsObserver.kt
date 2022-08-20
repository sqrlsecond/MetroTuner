package ru.makarovda.metrotuner.ui

interface IBeatsObserver {

    fun notifyBeatsObserver(beats: List<Boolean>)
}