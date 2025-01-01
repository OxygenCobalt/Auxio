/*
 * Copyright (c) 2021 Auxio Project
 * ArtistListFragment.kt is part of Auxio.
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
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.home.HomeViewModel
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.ListViewModel
import org.oxycblt.auxio.list.SelectableListListener
import org.oxycblt.auxio.list.adapter.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.recycler.ArtistViewHolder
import org.oxycblt.auxio.list.recycler.FastScrollRecyclerView
import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.positiveOrNull
import org.oxycblt.musikr.Artist
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.MusicParent
import org.oxycblt.musikr.Song

/**
 * A [ListFragment] that shows a list of [Artist]s.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class ArtistListFragment :
    ListFragment<Artist, FragmentHomeListBinding>(),
    FastScrollRecyclerView.PopupProvider,
    FastScrollRecyclerView.Listener {
    private val homeModel: HomeViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    override val listModel: ListViewModel by activityViewModels()
    override val musicModel: MusicViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()
    private val artistAdapter = ArtistAdapter(this)

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentHomeListBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentHomeListBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.homeRecycler.apply {
            id = R.id.home_artist_recycler
            adapter = artistAdapter
            popupProvider = this@ArtistListFragment
            listener = this@ArtistListFragment
        }

        binding.homeNoMusicPlaceholder.apply {
            setImageResource(R.drawable.ic_artist_48)
            contentDescription = getString(R.string.lbl_artists)
        }
        binding.homeNoMusicMsg.text = getString(R.string.lng_empty_artists)

        binding.homeNoMusicAction.setOnClickListener { homeModel.startChooseMusicLocations() }

        collectImmediately(homeModel.artistList, ::updateArtists)
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
        val artist = homeModel.artistList.value.getOrNull(pos) ?: return null
        // Change how we display the popup depending on the current sort mode.
        return when (homeModel.artistSort.mode) {
            // By Name -> Use Name
            is Sort.Mode.ByName -> artist.name.thumb()

            // Duration -> Use formatted duration
            is Sort.Mode.ByDuration -> artist.durationMs?.formatDurationMs(false)

            // Count -> Use song count
            is Sort.Mode.ByCount -> artist.songs.size.positiveOrNull()?.toString()

            // Unsupported sort, error gracefully
            else -> null
        }
    }

    override fun onFastScrollingChanged(isFastScrolling: Boolean) {
        homeModel.setFastScrolling(isFastScrolling)
    }

    override fun onRealClick(item: Artist) {
        detailModel.showArtist(item)
    }

    override fun onOpenMenu(item: Artist) {
        listModel.openMenu(R.menu.parent, item)
    }

    private fun updateArtists(artists: List<Artist>) {
        artistAdapter.update(artists, homeModel.artistInstructions.consume())
    }

    private fun updateNoMusicIndicator(empty: Boolean) {
        val binding = requireBinding()
        binding.homeRecycler.isInvisible = empty
        binding.homeNoMusic.isInvisible = !empty
    }

    private fun updateSelection(selection: List<Music>) {
        artistAdapter.setSelected(selection.filterIsInstanceTo(mutableSetOf()))
    }

    private fun updatePlayback(song: Song?, parent: MusicParent?, isPlaying: Boolean) {
        // Only highlight the artist if it is currently playing, and if the currently
        // playing song is also contained within.
        val artist = (parent as? Artist)?.takeIf { song?.run { artists.contains(it) } ?: false }
        artistAdapter.setPlaying(artist, isPlaying)
    }

    /**
     * A [SelectionIndicatorAdapter] that shows a list of [Artist]s using [ArtistViewHolder].
     *
     * @param listener An [SelectableListListener] to bind interactions to.
     */
    private class ArtistAdapter(private val listener: SelectableListListener<Artist>) :
        SelectionIndicatorAdapter<Artist, ArtistViewHolder>(ArtistViewHolder.DIFF_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ArtistViewHolder.from(parent)

        override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
            holder.bind(getItem(position), listener)
        }
    }
}
