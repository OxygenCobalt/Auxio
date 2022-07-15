/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.home.list

import android.os.Bundle
import android.view.View
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.MenuItemListener
import org.oxycblt.auxio.ui.recycler.MonoAdapter
import org.oxycblt.auxio.ui.recycler.SongViewHolder
import org.oxycblt.auxio.ui.recycler.SyncBackingData
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.formatDuration
import org.oxycblt.auxio.util.logEOrThrow

/**
 * A [HomeListFragment] for showing a list of [Song]s.
 * @author
 */
class SongListFragment : HomeListFragment<Song>() {
    private val homeAdapter = SongsAdapter(this)
    private val settings: Settings by lifecycleObject { binding -> Settings(binding.context) }

    override fun onBindingCreated(binding: FragmentHomeListBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.homeRecycler.apply {
            id = R.id.home_song_list
            adapter = homeAdapter
        }

        collectImmediately(homeModel.songs, homeAdapter.data::replaceList)
    }

    override fun getPopup(pos: Int): String? {
        val song = homeModel.songs.value[pos]

        // Change how we display the popup depending on the mode.
        // Note: We don't use the more correct individual artist name here, as sorts are largely
        // based off the names of the parent objects and not the child objects.
        return when (homeModel.getSortForDisplay(DisplayMode.SHOW_SONGS).mode) {
            // Name -> Use name
            is Sort.Mode.ByName -> song.sortName?.run { first().uppercase() }

            // Artist -> Use Artist Name
            is Sort.Mode.ByArtist -> song.album.artist.sortName?.run { first().uppercase() }

            // Album -> Use Album Name
            is Sort.Mode.ByAlbum -> song.album.sortName?.run { first().uppercase() }

            // Year -> Use Full Year
            is Sort.Mode.ByYear -> song.album.date?.resolveYear(requireContext())

            // Duration -> Use formatted duration
            is Sort.Mode.ByDuration -> song.durationSecs.formatDuration(false)

            // Unsupported sort, error gracefully
            else -> null
        }
    }

    override fun onItemClick(item: Item) {
        check(item is Song)
        playbackModel.play(item, settings.libPlaybackMode)
    }

    override fun onOpenMenu(item: Item, anchor: View) {
        when (item) {
            is Song -> musicMenu(anchor, R.menu.menu_song_actions, item)
            else -> logEOrThrow("Unexpected datatype when opening menu: ${item::class.java}")
        }
    }

    inner class SongsAdapter(listener: MenuItemListener) :
        MonoAdapter<Song, MenuItemListener, SongViewHolder>(listener) {
        override val data = SyncBackingData(this, SongViewHolder.DIFFER)
        override val creator = SongViewHolder.CREATOR
    }
}
