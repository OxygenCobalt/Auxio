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
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.ui.ArtistViewHolder
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Item
import org.oxycblt.auxio.ui.MenuItemListener
import org.oxycblt.auxio.ui.MonoAdapter
import org.oxycblt.auxio.ui.PrimitiveBackingData
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.util.formatDuration
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A [HomeListFragment] for showing a list of [Artist]s.
 * @author
 */
class ArtistListFragment : HomeListFragment<Artist>() {
    private val homeAdapter = ArtistAdapter(this)

    override fun setupRecycler(recycler: RecyclerView) {
        recycler.apply {
            id = R.id.home_artist_list
            adapter = homeAdapter
        }

        homeModel.artists.observe(viewLifecycleOwner) { list -> homeAdapter.data.submitList(list) }
    }

    override fun getPopup(pos: Int): String? {
        val artist = unlikelyToBeNull(homeModel.artists.value)[pos]

        // Change how we display the popup depending on the mode.
        return when (homeModel.getSortForDisplay(DisplayMode.SHOW_ARTISTS)) {
            // By Name -> Use Name
            is Sort.ByName -> artist.sortName?.run { first().uppercase() }

            // Duration -> Use formatted duration
            is Sort.ByDuration -> artist.durationSecs.formatDuration(false)

            // Count -> Use song count
            is Sort.ByCount -> artist.songs.size.toString()

            // Unsupported sort, error gracefully
            else -> null
        }
    }

    override fun onItemClick(item: Item) {
        check(item is Music)
        navModel.exploreNavigateTo(item)
    }

    override fun onOpenMenu(item: Item, anchor: View) {
        newMenu(anchor, item)
    }

    class ArtistAdapter(listener: MenuItemListener) :
        MonoAdapter<Artist, MenuItemListener, ArtistViewHolder>(listener) {
        override val data = PrimitiveBackingData<Artist>(this)
        override val creator = ArtistViewHolder.CREATOR
    }
}
