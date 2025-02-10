package org.oxycblt.auxio.home.tabs

import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy

class NamedTabStrategy(private val homeTabs: List<Tab>) : TabConfigurationStrategy {
    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        tab.setText(homeTabs[position].type.nameRes)
    }
}