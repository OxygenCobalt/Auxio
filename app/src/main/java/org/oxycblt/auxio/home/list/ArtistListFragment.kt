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
import android.view.ViewGroup
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Sort
import org.oxycblt.auxio.music.formatDurationMs
import org.oxycblt.auxio.ui.recycler.ArtistViewHolder
import org.oxycblt.auxio.ui.recycler.IndicatorAdapter
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.MenuItemListener
import org.oxycblt.auxio.ui.recycler.SyncListDiffer
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.nonZeroOrNull

/**
 * A [HomeListFragment] for showing a list of [Artist]s.
 * @author OxygenCobalt
 */
class ArtistListFragment : HomeListFragment<Artist>() {
    private val homeAdapter = ArtistAdapter(this)

    override fun onBindingCreated(binding: FragmentHomeListBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.homeRecycler.apply {
            id = R.id.home_artist_list
            adapter = homeAdapter
        }

        collectImmediately(homeModel.artists, homeAdapter::replaceList)
        collectImmediately(playbackModel.parent, playbackModel.isPlaying, ::handleParent)
    }

    override fun getPopup(pos: Int): String? {
        val artist = homeModel.artists.value[pos]

        // Change how we display the popup depending on the mode.
        return when (homeModel.getSortForTab(MusicMode.ARTISTS).mode) {
            // By Name -> Use Name
            is Sort.Mode.ByName -> artist.collationKey?.run { sourceString.first().uppercase() }

            // Duration -> Use formatted duration
            is Sort.Mode.ByDuration -> artist.durationMs?.formatDurationMs(false)

            // Count -> Use song count
            is Sort.Mode.ByCount -> artist.songs.size.nonZeroOrNull()?.toString()

            // Unsupported sort, error gracefully
            else -> null
        }
    }

    override fun onItemClick(item: Item) {
        check(item is Artist) { "Unexpected datatype: ${item::class.java}" }
        navModel.exploreNavigateTo(item)
    }

    override fun onOpenMenu(item: Item, anchor: View) {
        check(item is Artist) { "Unexpected datatype: ${item::class.java}" }
        musicMenu(anchor, R.menu.menu_genre_artist_actions, item)
    }

    private fun handleParent(parent: MusicParent?, isPlaying: Boolean) {
        if (parent is Artist) {
            homeAdapter.updateIndicator(parent, isPlaying)
        } else {
            // Ignore playback not from artists
            homeAdapter.updateIndicator(null, isPlaying)
        }
    }

    private class ArtistAdapter(private val listener: MenuItemListener) :
        IndicatorAdapter<ArtistViewHolder>() {
        private val differ = SyncListDiffer(this, ArtistViewHolder.DIFFER)

        override val currentList: List<Item>
            get() = differ.currentList

        override fun getItemCount() = differ.currentList.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ArtistViewHolder.new(parent)

        override fun onBindViewHolder(
            holder: ArtistViewHolder,
            position: Int,
            payloads: List<Any>
        ) {
            super.onBindViewHolder(holder, position, payloads)

            if (payloads.isEmpty()) {
                holder.bind(differ.currentList[position], listener)
            }
        }

        fun replaceList(newList: List<Artist>) {
            differ.replaceList(newList)
        }
    }
}
