package ru.makarovda.metrotuner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import ru.makarovda.metrotuner.R

class FragmentVP: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val pager = view.findViewById<ViewPager2>(R.id.view_pager)
        pager?.adapter = FragmentsVPAdapter(this)

        TabLayoutMediator(view.findViewById(R.id.tab_layout), pager) { tab, position ->
            tab.text = resources.getStringArray(R.array.tabs_names)[position]
        }.attach()

        ViewCompat.setOnApplyWindowInsetsListener(view) { view, insets ->
            val innerPadding = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
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