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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.home.HomeViewModel
import org.oxycblt.auxio.list.*
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.recycler.ArtistViewHolder
import org.oxycblt.auxio.list.recycler.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.recycler.SyncListDiffer
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Sort
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.nonZeroOrNull

/**
 * A [HomeListFragment] for showing a list of [Artist]s.
 * @author OxygenCobalt
 */
class ArtistListFragment : ListFragment<FragmentHomeListBinding>() {
    private val homeModel: HomeViewModel by activityViewModels()

    private val homeAdapter =
        ArtistAdapter(ItemSelectCallback(::handleClick, ::handleOpenMenu, ::handleSelect))

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentHomeListBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentHomeListBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.homeRecycler.apply {
            id = R.id.home_artist_recycler
            adapter = homeAdapter

            popupProvider = ::updatePopup
            fastScrollCallback = homeModel::setFastScrolling
        }

        collectImmediately(homeModel.artists, homeAdapter::replaceList)
        collectImmediately(selectionModel.selected, homeAdapter::setSelectedItems)
        collectImmediately(playbackModel.parent, playbackModel.isPlaying, ::updatePlayback)
    }

    override fun onDestroyBinding(binding: FragmentHomeListBinding) {
        super.onDestroyBinding(binding)
        binding.homeRecycler.adapter = null
    }

    private fun updatePopup(pos: Int): String? {
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

    override fun onRealClick(music: Music) {
        check(music is Artist) { "Unexpected datatype: ${music::class.java}" }
        navModel.exploreNavigateTo(music)
    }

    private fun handleOpenMenu(item: Item, anchor: View) {
        check(item is Artist) { "Unexpected datatype: ${item::class.java}" }
        openMusicMenu(anchor, R.menu.menu_artist_actions, item)
    }

    private fun updatePlayback(parent: MusicParent?, isPlaying: Boolean) {
        if (parent is Artist) {
            homeAdapter.setPlayingItem(parent, isPlaying)
        } else {
            // Ignore playback not from artists
            homeAdapter.setPlayingItem(null, isPlaying)
        }
    }

    private class ArtistAdapter(private val callback: ItemSelectCallback) :
        SelectionIndicatorAdapter<ArtistViewHolder>() {
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
                holder.bind(differ.currentList[position], callback)
            }
        }

        fun replaceList(newList: List<Artist>) {
            differ.replaceList(newList)
        }
    }
}
