package ru.makarovda.metrotuner.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.makarovda.metrotuner.ui.metronome.MetronomeFragment
import ru.makarovda.metrotuner.ui.tuner.TunerFragment

class FragmentsVPAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> MetronomeFragment()
            1 -> TunerFragment()
            else -> MetronomeFragment()
        }
    }

}