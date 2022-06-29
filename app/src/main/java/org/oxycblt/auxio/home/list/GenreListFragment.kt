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
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.ui.recycler.GenreViewHolder
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.MenuItemListener
import org.oxycblt.auxio.ui.recycler.MonoAdapter
import org.oxycblt.auxio.ui.recycler.SyncBackingData
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.formatDuration
import org.oxycblt.auxio.util.logEOrThrow

/**
 * A [HomeListFragment] for showing a list of [Genre]s.
 * @author
 */
class GenreListFragment : HomeListFragment<Genre>() {
    private val homeAdapter = GenreAdapter(this)

    override fun onBindingCreated(binding: FragmentHomeListBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.homeRecycler.apply {
            id = R.id.home_genre_list
            adapter = homeAdapter
        }

        collectImmediately(homeModel.genres, homeAdapter.data::replaceList)
    }

    override fun getPopup(pos: Int): String? {
        val genre = homeModel.genres.value[pos]

        // Change how we display the popup depending on the mode.
        return when (homeModel.getSortForDisplay(DisplayMode.SHOW_GENRES).mode) {
            // By Name -> Use Name
            is Sort.Mode.ByName -> genre.sortName?.run { first().uppercase() }

            // Duration -> Use formatted duration
            is Sort.Mode.ByDuration -> genre.durationSecs.formatDuration(false)

            // Count -> Use song count
            is Sort.Mode.ByCount -> genre.songs.size.toString()

            // Unsupported sort, error gracefully
            else -> null
        }
    }

    override fun onItemClick(item: Item) {
        check(item is Music)
        navModel.exploreNavigateTo(item)
    }

    override fun onOpenMenu(item: Item, anchor: View) {
        when (item) {
            is Genre -> musicMenu(anchor, R.menu.menu_genre_artist_actions, item)
            else -> logEOrThrow("Unexpected datatype when opening menu: ${item::class.java}")
        }
    }

    class GenreAdapter(listener: MenuItemListener) :
        MonoAdapter<Genre, MenuItemListener, GenreViewHolder>(listener) {
        override val data = SyncBackingData(this, GenreViewHolder.DIFFER)
        override val creator = GenreViewHolder.CREATOR
    }
}
