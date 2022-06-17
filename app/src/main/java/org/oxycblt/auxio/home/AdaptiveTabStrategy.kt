/*
 * Copyright (c) 2022 Auxio Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
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
 * @author OxygenCobalt
 */
class AdaptiveTabStrategy(context: Context, private val homeModel: HomeViewModel) :
    TabLayoutMediator.TabConfigurationStrategy {
    private val width = context.resources.configuration.smallestScreenWidthDp

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        val tabMode = homeModel.tabs[position]

        when {
            width < 370 -> {
                logD("Using icon-only configuration")
                tab.setIcon(tabMode.icon).setContentDescription(tabMode.string)
            }
            width < 600 -> {
                logD("Using text-only configuration")
                tab.setText(tabMode.string)
            }
            else -> {
                logD("Using icon-and-text configuration")
                tab.setIcon(tabMode.icon).setText(tabMode.string)
            }
        }
    }
}
