package ru.makarovda.metrotuner.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

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