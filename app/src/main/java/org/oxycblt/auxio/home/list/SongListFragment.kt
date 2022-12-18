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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import java.util.Formatter
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.home.HomeViewModel
import org.oxycblt.auxio.list.*
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.recycler.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.recycler.SongViewHolder
import org.oxycblt.auxio.list.recycler.SyncListDiffer
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.Sort
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.playback.secsToMs
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.context

/**
 * A [HomeListFragment] for showing a list of [Song]s.
 * @author OxygenCobalt
 */
class SongListFragment : ListFragment<FragmentHomeListBinding>() {
    private val homeModel: HomeViewModel by activityViewModels()

    private val homeAdapter =
        SongAdapter(ItemSelectCallback(::handleClick, ::handleOpenMenu, ::handleSelect))

    private val settings: Settings by lifecycleObject { binding -> Settings(binding.context) }

    private val formatterSb = StringBuilder(50)
    private val formatter = Formatter(formatterSb)

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentHomeListBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentHomeListBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.homeRecycler.apply {
            id = R.id.home_song_recycler
            adapter = homeAdapter

            popupProvider = ::updatePopup
            fastScrollCallback = homeModel::setFastScrolling
        }

        collectImmediately(homeModel.songs, homeAdapter::replaceList)
        collectImmediately(selectionModel.selected, homeAdapter::setSelectedItems)
        collectImmediately(
            playbackModel.song, playbackModel.parent, playbackModel.isPlaying, ::updatePlayback)
    }

    override fun onDestroyBinding(binding: FragmentHomeListBinding) {
        super.onDestroyBinding(binding)
        binding.homeRecycler.adapter = null
    }

    private fun updatePopup(pos: Int): String? {
        val song = homeModel.songs.value[pos]

        // Change how we display the popup depending on the mode.
        // Note: We don't use the more correct individual artist name here, as sorts are largely
        // based off the names of the parent objects and not the child objects.
        return when (homeModel.getSortForTab(MusicMode.SONGS).mode) {
            // Name -> Use name
            is Sort.Mode.ByName -> song.collationKey?.run { sourceString.first().uppercase() }

            // Artist -> Use name of first artist
            is Sort.Mode.ByArtist ->
                song.album.artists[0].collationKey?.run { sourceString.first().uppercase() }

            // Album -> Use Album Name
            is Sort.Mode.ByAlbum ->
                song.album.collationKey?.run { sourceString.first().uppercase() }

            // Year -> Use Full Year
            is Sort.Mode.ByDate -> song.album.date?.resolveDate(requireContext())

            // Duration -> Use formatted duration
            is Sort.Mode.ByDuration -> song.durationMs.formatDurationMs(false)

            // Last added -> Format as date
            is Sort.Mode.ByDateAdded -> {
                val dateAddedMillis = song.dateAdded.secsToMs()
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

    override fun onRealClick(music: Music) {
        check(music is Song) { "Unexpected datatype: ${music::class.java}" }
        when (settings.libPlaybackMode) {
            MusicMode.SONGS -> playbackModel.playFromAll(music)
            MusicMode.ALBUMS -> playbackModel.playFromAlbum(music)
            MusicMode.ARTISTS -> playbackModel.playFromArtist(music)
            else -> error("Unexpected playback mode: ${settings.libPlaybackMode}")
        }
    }

    private fun handleOpenMenu(item: Item, anchor: View) {
        check(item is Song) { "Unexpected datatype: ${item::class.java}" }
        openMusicMenu(anchor, R.menu.menu_song_actions, item)
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        if (parent == null) {
            homeAdapter.setPlayingItem(song, isPlaying)
        } else {
            // Ignore playback that is not from all songs
            homeAdapter.setPlayingItem(null, isPlaying)
        }
    }

    private class SongAdapter(private val callback: ItemSelectCallback) :
        SelectionIndicatorAdapter<SongViewHolder>() {
        private val differ = SyncListDiffer(this, SongViewHolder.DIFFER)

        override val currentList: List<Item>
            get() = differ.currentList

        override fun getItemCount() = differ.currentList.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            SongViewHolder.new(parent)

        override fun onBindViewHolder(holder: SongViewHolder, position: Int, payloads: List<Any>) {
            super.onBindViewHolder(holder, position, payloads)

            if (payloads.isEmpty()) {
                holder.bind(differ.currentList[position], callback)
            }
        }

        fun replaceList(newList: List<Song>) {
            differ.replaceList(newList)
        }
    }
}
