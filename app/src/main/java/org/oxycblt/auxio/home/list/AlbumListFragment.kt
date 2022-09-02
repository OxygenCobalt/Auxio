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
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import java.util.*
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.ui.recycler.AlbumViewHolder
import org.oxycblt.auxio.ui.recycler.IndicatorAdapter
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.MenuItemListener
import org.oxycblt.auxio.ui.recycler.SyncListDiffer
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.formatDurationMs
import org.oxycblt.auxio.util.secsToMs

/**
 * A [HomeListFragment] for showing a list of [Album]s.
 * @author
 */
class AlbumListFragment : HomeListFragment<Album>() {
    private val homeAdapter = AlbumAdapter(this)
    private val formatterSb = StringBuilder(32)
    private val formatter = Formatter(formatterSb)

    override fun onBindingCreated(binding: FragmentHomeListBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.homeRecycler.apply {
            id = R.id.home_album_list
            adapter = homeAdapter
        }

        collectImmediately(homeModel.albums, homeAdapter::replaceList)
        collectImmediately(playbackModel.parent, playbackModel.isPlaying, ::handleParent)
    }

    override fun getPopup(pos: Int): String? {
        val album = homeModel.albums.value[pos]

        // Change how we display the popup depending on the mode.
        return when (homeModel.getSortForDisplay(DisplayMode.SHOW_ALBUMS).mode) {
            // By Name -> Use Name
            is Sort.Mode.ByName -> album.sortName?.run { first().uppercase() }

            // By Artist -> Use Artist Name
            is Sort.Mode.ByArtist -> album.artist.sortName?.run { first().uppercase() }

            // Year -> Use Full Year
            is Sort.Mode.ByYear -> album.date?.resolveYear(requireContext())

            // Duration -> Use formatted duration
            is Sort.Mode.ByDuration -> album.durationMs.formatDurationMs(false)

            // Count -> Use song count
            is Sort.Mode.ByCount -> album.songs.size.toString()

            // Last added -> Format as date
            is Sort.Mode.ByDateAdded -> {
                val dateAddedMillis = album.dateAdded.secsToMs()
                formatterSb.setLength(0)
                DateUtils.formatDateRange(
                        context,
                        formatter,
                        dateAddedMillis,
                        dateAddedMillis,
                        DateUtils.FORMAT_ABBREV_ALL)
                    .toString()
            }

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
            is Album -> musicMenu(anchor, R.menu.menu_album_actions, item)
            else -> error("Unexpected datatype when opening menu: ${item::class.java}")
        }
    }

    private fun handleParent(parent: MusicParent?, isPlaying: Boolean) {
        if (parent is Album) {
            homeAdapter.updateIndicator(parent, isPlaying)
        } else {
            // Ignore playback not from albums
            homeAdapter.updateIndicator(null, isPlaying)
        }
    }

    private class AlbumAdapter(private val listener: MenuItemListener) :
        IndicatorAdapter<AlbumViewHolder>() {
        private val differ = SyncListDiffer(this, AlbumViewHolder.DIFFER)

        override val currentList: List<Item>
            get() = differ.currentList

        override fun getItemCount() = differ.currentList.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            AlbumViewHolder.new(parent)

        override fun onBindViewHolder(holder: AlbumViewHolder, position: Int, payloads: List<Any>) {
            super.onBindViewHolder(holder, position, payloads)

            if (payloads.isEmpty()) {
                holder.bind(differ.currentList[position], listener)
            }
        }

        fun replaceList(newList: List<Album>) {
            differ.replaceList(newList)
        }
    }
}
