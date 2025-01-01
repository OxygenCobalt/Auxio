/*
 * Copyright (c) 2021 Auxio Project
 * AlbumListFragment.kt is part of Auxio.
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
import androidx.core.view.isInvisible
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import java.util.Formatter
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.home.HomeViewModel
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.ListViewModel
import org.oxycblt.auxio.list.SelectableListListener
import org.oxycblt.auxio.list.adapter.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.recycler.AlbumViewHolder
import org.oxycblt.auxio.list.recycler.FastScrollRecyclerView
import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.resolve
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.playback.secsToMs
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.musikr.Album
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.MusicParent
import org.oxycblt.musikr.Song

/**
 * A [ListFragment] that shows a list of [Album]s.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class AlbumListFragment :
    ListFragment<Album, FragmentHomeListBinding>(),
    FastScrollRecyclerView.Listener,
    FastScrollRecyclerView.PopupProvider {
    private val homeModel: HomeViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    override val listModel: ListViewModel by activityViewModels()
    override val musicModel: MusicViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()
    private val albumAdapter = AlbumAdapter(this)
    // Save memory by re-using the same formatter and string builder when creating popup text
    private val formatterSb = StringBuilder(64)
    private val formatter = Formatter(formatterSb)

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentHomeListBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentHomeListBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.homeRecycler.apply {
            id = R.id.home_album_recycler
            adapter = albumAdapter
            popupProvider = this@AlbumListFragment
            listener = this@AlbumListFragment
        }

        binding.homeNoMusicPlaceholder.apply {
            setImageResource(R.drawable.ic_album_48)
            contentDescription = getString(R.string.lbl_albums)
        }
        binding.homeNoMusicMsg.text = getString(R.string.lng_empty_albums)

        binding.homeNoMusicAction.setOnClickListener { homeModel.startChooseMusicLocations() }

        collectImmediately(homeModel.albumList, ::updateAlbums)
        collectImmediately(homeModel.empty, ::updateNoMusicIndicator)
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
        val album = homeModel.albumList.value.getOrNull(pos) ?: return null
        // Change how we display the popup depending on the current sort mode.
        return when (homeModel.albumSort.mode) {
            // By Name -> Use Name
            is Sort.Mode.ByName -> album.name.thumb()

            // By Artist -> Use name of first artist
            is Sort.Mode.ByArtist -> album.artists[0].name.thumb()

            // Date -> Use minimum date (Maximum dates are not sorted by, so showing them is odd)
            is Sort.Mode.ByDate -> album.dates?.run { min.resolve(requireContext()) }

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

    override fun onFastScrollingChanged(isFastScrolling: Boolean) {
        homeModel.setFastScrolling(isFastScrolling)
    }

    override fun onRealClick(item: Album) {
        detailModel.showAlbum(item)
    }

    override fun onOpenMenu(item: Album) {
        listModel.openMenu(R.menu.album, item)
    }

    private fun updateAlbums(albums: List<Album>) {
        albumAdapter.update(albums, homeModel.albumInstructions.consume())
    }

    private fun updateNoMusicIndicator(empty: Boolean) {
        val binding = requireBinding()
        binding.homeRecycler.isInvisible = empty
        binding.homeNoMusic.isInvisible = !empty
    }

    private fun updateSelection(selection: List<Music>) {
        albumAdapter.setSelected(selection.filterIsInstanceTo(mutableSetOf()))
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        // Only highlight the album if it is currently playing, and if the currently
        // playing song is also contained within.
        val album = (parent as? Album)?.takeIf { song?.album == it }
        albumAdapter.setPlaying(album, isPlaying)
    }

    /**
     * A [SelectionIndicatorAdapter] that shows a list of [Album]s using [AlbumViewHolder].
     *
     * @param listener An [SelectableListListener] to bind interactions to.
     */
    private class AlbumAdapter(private val listener: SelectableListListener<Album>) :
        SelectionIndicatorAdapter<Album, AlbumViewHolder>(AlbumViewHolder.DIFF_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            AlbumViewHolder.from(parent)

        override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
            holder.bind(getItem(position), listener)
        }
    }
}
