/*
 * Copyright (c) 2022 Auxio Project
 * AdaptiveTabStrategy.kt is part of Auxio.
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
 
package org.oxycblt.auxio.home.tabs

import android.content.Context
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.MusicType

/**
 * A [TabLayoutMediator.TabConfigurationStrategy] that uses larger/smaller tab configurations
 * depending on the screen configuration.
 *
 * @param context [Context] required to obtain window information
 * @param tabs Current tab configuration from settings
 * @author Alexander Capehart (OxygenCobalt)
 */
class AdaptiveTabStrategy(context: Context, private val tabs: List<MusicType>) :
    TabLayoutMediator.TabConfigurationStrategy {
    private val width = context.resources.configuration.smallestScreenWidthDp

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        val homeTab = tabs[position]
        val icon =
            when (homeTab) {
                MusicType.SONGS -> R.drawable.ic_song_24
                MusicType.ALBUMS -> R.drawable.ic_album_24
                MusicType.ARTISTS -> R.drawable.ic_artist_24
                MusicType.GENRES -> R.drawable.ic_genre_24
                MusicType.PLAYLISTS -> R.drawable.ic_playlist_24
            }

        // Use expected sw* size thresholds when choosing a configuration.
        when {
            // On small screens, only display an icon.
            width < 370 -> tab.setIcon(icon).setContentDescription(homeTab.nameRes)
            // On large screens, display an icon and text.
            width < 600 -> tab.setText(homeTab.nameRes)
            // On medium-size screens, display text.
            else -> tab.setIcon(icon).setText(homeTab.nameRes)
        }
    }
}
