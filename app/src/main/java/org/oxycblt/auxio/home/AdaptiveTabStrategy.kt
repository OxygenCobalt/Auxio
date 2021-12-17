package org.oxycblt.auxio.home

import android.content.Context
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.oxycblt.auxio.util.logD

class AdaptiveTabStrategy(
    context: Context,
    private val homeModel: HomeViewModel
) : TabLayoutMediator.TabConfigurationStrategy {
    private val screenSize = context.resources.configuration.screenWidthDp

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        val tabMode = homeModel.tabs[position]

        when {
            screenSize < 370 ->
                tab.setIcon(tabMode.icon)
                    .setContentDescription(tabMode.string)

            screenSize < 600 -> tab.setText(tabMode.string)

            else ->
                tab.setIcon(tabMode.icon)
                    .setText(tabMode.string)
        }
    }
}
