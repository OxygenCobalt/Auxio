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
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.ui.AlbumViewHolder
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Item
import org.oxycblt.auxio.ui.MenuItemListener
import org.oxycblt.auxio.ui.MonoAdapter
import org.oxycblt.auxio.ui.PrimitiveBackingData
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A [HomeListFragment] for showing a list of [Album]s.
 * @author
 */
class AlbumListFragment : HomeListFragment<Album>() {
    private val homeAdapter = AlbumAdapter(this)

    override fun setupRecycler(recycler: RecyclerView) {
        recycler.apply {
            id = R.id.home_album_list
            adapter = homeAdapter
        }

        homeModel.albums.observe(viewLifecycleOwner) { list -> homeAdapter.data.submitList(list) }
    }

    override fun getPopup(pos: Int): String? {
        val album = unlikelyToBeNull(homeModel.albums.value)[pos]

        // Change how we display the popup depending on the mode.
        return when (homeModel.getSortForDisplay(DisplayMode.SHOW_ALBUMS)) {
            // By Name -> Use Name
            is Sort.ByName -> album.sortName.first().uppercase()

            // By Artist -> Use Artist Name
            is Sort.ByArtist -> album.artist.sortName?.run { first().uppercase() }

            // Year -> Use Full Year
            is Sort.ByYear -> album.year?.toString()

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

    class AlbumAdapter(listener: MenuItemListener) :
        MonoAdapter<Album, MenuItemListener, AlbumViewHolder>(listener) {
        override val data = PrimitiveBackingData<Album>(this)
        override val creator = AlbumViewHolder.CREATOR
    }
}
