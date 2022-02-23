package org.oxycblt.auxio.home

import android.content.Context
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.oxycblt.auxio.util.logD

/**
 * A tag configuration strategy that automatically adapts the tab layout to the screen size.
 * - On small screens, use only an icon
 * - On medium screens, use only text
 * - On large screens, use text and an icon
 */
class AdaptiveTabStrategy(
    context: Context,
    private val homeModel: HomeViewModel
) : TabLayoutMediator.TabConfigurationStrategy {
    private val width = context.resources.configuration.smallestScreenWidthDp

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        val tabMode = homeModel.tabs[position]

        when {
            width < 370 -> {
                logD("Using icon-only configuration")
                tab.setIcon(tabMode.icon)
                    .setContentDescription(tabMode.string)
            }

            width < 640 -> {
                logD("Using text-only configuration")
                tab.setText(tabMode.string)
            }

            else -> {
                logD("Using icon-and-text configuration")
                tab.setIcon(tabMode.icon)
                    .setText(tabMode.string)
            }
        }
    }
}
