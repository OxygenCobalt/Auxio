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

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Item
import org.oxycblt.auxio.ui.MenuItemListener
import org.oxycblt.auxio.ui.MonoAdapter
import org.oxycblt.auxio.ui.PrimitiveBackingData
import org.oxycblt.auxio.ui.SongViewHolder
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.util.formatDuration
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A [HomeListFragment] for showing a list of [Song]s.
 * @author
 */
class SongListFragment : HomeListFragment<Song>() {
    private val homeAdapter = SongsAdapter(this)

    override fun setupRecycler(recycler: RecyclerView) {
        recycler.apply {
            id = R.id.home_song_list
            adapter = homeAdapter
        }

        homeModel.songs.observe(viewLifecycleOwner) { list -> homeAdapter.data.submitList(list) }
    }

    override fun getPopup(pos: Int): String? {
        val song = unlikelyToBeNull(homeModel.songs.value)[pos]

        // Change how we display the popup depending on the mode.
        // We don't use the more correct resolve(Model)Name here, as sorts are largely
        // based off the names of the parent objects and not the child objects.
        return when (homeModel.getSortForDisplay(DisplayMode.SHOW_SONGS)) {
            // Name -> Use name
            is Sort.ByName -> song.sortName.first().uppercase()

            // Artist -> Use Artist Name
            is Sort.ByArtist -> song.album.artist.sortName?.run { first().uppercase() }

            // Album -> Use Album Name
            is Sort.ByAlbum -> song.album.sortName.first().uppercase()

            // Year -> Use Full Year
            is Sort.ByYear -> song.album.year?.toString()

            // Duration -> Use formatted duration
            is Sort.ByDuration -> song.durationSecs.formatDuration(false)

            // Unsupported sort, error gracefully
            else -> null
        }
    }

    override fun onItemClick(item: Item) {
        check(item is Song)
        playbackModel.play(item)
    }

    override fun onOpenMenu(item: Item, anchor: View) {
        newMenu(anchor, item)
    }

    inner class SongsAdapter(listener: MenuItemListener) :
        MonoAdapter<Song, MenuItemListener, SongViewHolder>(listener) {
        override val data = PrimitiveBackingData<Song>(this)
        override val creator = SongViewHolder.CREATOR
    }
}
