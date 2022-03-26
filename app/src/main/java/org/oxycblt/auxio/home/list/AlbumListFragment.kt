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
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.home.HomeFragmentDirections
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.ui.AlbumViewHolder
import org.oxycblt.auxio.ui.BindingViewHolder
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Item
import org.oxycblt.auxio.ui.MenuItemListener
import org.oxycblt.auxio.ui.MonoAdapter
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.ui.sliceArticle

/**
 * A [HomeListFragment] for showing a list of [Album]s.
 * @author
 */
class AlbumListFragment : HomeListFragment<Album>() {
    override val recyclerId: Int = R.id.home_album_list
    override val homeAdapter = AlbumAdapter(this)
    override val homeData: LiveData<List<Album>>
        get() = homeModel.albums

    override fun getPopup(pos: Int): String? {
        val album = homeModel.albums.value!![pos]

        // Change how we display the popup depending on the mode.
        return when (homeModel.getSortForDisplay(DisplayMode.SHOW_ALBUMS)) {
            // By Name -> Use Name
            is Sort.ByName -> album.resolvedName.sliceArticle().first().uppercase()

            // By Artist -> Use Artist Name
            is Sort.ByArtist -> album.artist.resolvedName.sliceArticle().first().uppercase()

            // Year -> Use Full Year
            is Sort.ByYear -> album.year?.toString() ?: getString(R.string.def_date)

            // Unsupported sort, error gracefully
            else -> null
        }
    }

    override fun onItemClick(item: Item) {
        check(item is Album)
        findNavController().navigate(HomeFragmentDirections.actionShowAlbum(item.id))
    }

    override fun onOpenMenu(item: Item, anchor: View) {
        newMenu(anchor, item)
    }

    class AlbumAdapter(listener: MenuItemListener) :
        MonoAdapter<Album, MenuItemListener, AlbumViewHolder>(listener, AlbumViewHolder.DIFFER) {
        override val creator: BindingViewHolder.Creator<AlbumViewHolder>
            get() = AlbumViewHolder.CREATOR
    }
}
