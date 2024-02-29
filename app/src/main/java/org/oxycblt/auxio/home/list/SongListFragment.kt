/*
 * Copyright (c) 2021 Auxio Project
 * SongListFragment.kt is part of Auxio.
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
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import java.util.Formatter
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.home.HomeViewModel
import org.oxycblt.auxio.home.fastscroll.FastScrollRecyclerView
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.ListViewModel
import org.oxycblt.auxio.list.SelectableListListener
import org.oxycblt.auxio.list.adapter.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.recycler.SongViewHolder
import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.playback.secsToMs
import org.oxycblt.auxio.util.collectImmediately

/**
 * A [ListFragment] that shows a list of [Song]s.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class SongListFragment :
    ListFragment<Song, FragmentHomeListBinding>(),
    FastScrollRecyclerView.PopupProvider,
    FastScrollRecyclerView.Listener {
    private val homeModel: HomeViewModel by activityViewModels()
    override val listModel: ListViewModel by activityViewModels()
    override val musicModel: MusicViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()
    private val songAdapter = SongAdapter(this)
    // Save memory by re-using the same formatter and string builder when creating popup text
    private val formatterSb = StringBuilder(64)
    private val formatter = Formatter(formatterSb)

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentHomeListBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentHomeListBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.homeRecycler.apply {
            id = R.id.home_song_recycler
            adapter = songAdapter
            popupProvider = this@SongListFragment
            listener = this@SongListFragment
        }

        collectImmediately(homeModel.songList, ::updateSongs)
        collectImmediately(listModel.selected, ::updateSelection)
        collectImmediately(
            playbackModel.song, playbackModel.parent, playbackModel.isPlaying, ::updatePlayback)
    }

    override fun onDestroyBinding(binding: FragmentHomeListBinding) {
        super.onDestroyBinding(binding)
        binding.homeRecycler.apply {
            adapter = null
            popupProvider = null
            listener = null
        }
    }

    override fun getPopup(pos: Int): String? {
        val song = homeModel.songList.value.getOrNull(pos) ?: return null
        // Change how we display the popup depending on the current sort mode.
        // Note: We don't use the more correct individual artist name here, as sorts are largely
        // based off the names of the parent objects and not the child objects.
        return when (homeModel.songSort.mode) {
            // Name -> Use name
            is Sort.Mode.ByName -> song.name.thumb

            // Artist -> Use name of first artist
            is Sort.Mode.ByArtist -> song.album.artists[0].name.thumb

            // Album -> Use Album Name
            is Sort.Mode.ByAlbum -> song.album.name.thumb

            // Year -> Use Full Year
            is Sort.Mode.ByDate -> song.album.dates?.resolveDate(requireContext())

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

    override fun onFastScrollingChanged(isFastScrolling: Boolean) {
        homeModel.setFastScrolling(isFastScrolling)
    }

    override fun onRealClick(item: Song) {
        playbackModel.play(item, homeModel.playWith)
    }

    override fun onOpenMenu(item: Song) {
        listModel.openMenu(R.menu.song, item, homeModel.playWith)
    }

    private fun updateSongs(songs: List<Song>) {
        songAdapter.update(songs, homeModel.songInstructions.consume())
    }

    private fun updateSelection(selection: List<Music>) {
        songAdapter.setSelected(selection.filterIsInstanceTo(mutableSetOf()))
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        // Only indicate playback that is from all songs
        songAdapter.setPlaying(song.takeIf { parent == null }, isPlaying)
    }

    /**
     * A [SelectionIndicatorAdapter] that shows a list of [Song]s using [SongViewHolder].
     *
     * @param listener An [SelectableListListener] to bind interactions to.
     */
    private class SongAdapter(private val listener: SelectableListListener<Song>) :
        SelectionIndicatorAdapter<Song, SongViewHolder>(SongViewHolder.DIFF_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            SongViewHolder.from(parent)

        override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
            holder.bind(getItem(position), listener)
        }
    }
}
