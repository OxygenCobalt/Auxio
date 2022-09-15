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
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.MusicMode
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

        val icon: Int
        val string: Int

        when (tabMode) {
            MusicMode.SONGS -> {
                icon = R.drawable.ic_song_24
                string = R.string.lbl_songs
            }
            MusicMode.ALBUMS -> {
                icon = R.drawable.ic_album_24
                string = R.string.lbl_albums
            }
            MusicMode.ARTISTS -> {
                icon = R.drawable.ic_artist_24
                string = R.string.lbl_artists
            }
            MusicMode.GENRES -> {
                icon = R.drawable.ic_genre_24
                string = R.string.lbl_genres
            }
        }

        when {
            width < 370 -> {
                logD("Using icon-only configuration")
                tab.setIcon(icon).setContentDescription(string)
            }
            width < 600 -> {
                logD("Using text-only configuration")
                tab.setText(string)
            }
            else -> {
                logD("Using icon-and-text configuration")
                tab.setIcon(icon).setText(string)
            }
        }
    }
}
