/*
 * Copyright (c) 2023 Auxio Project
 * PlaylistListFragment.kt is part of Auxio.
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
import org.oxycblt.auxio.home.fastscroll.FastScrollRecyclerView
import org.oxycblt.auxio.list.*
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.Sort
import org.oxycblt.auxio.list.adapter.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.recycler.PlaylistViewHolder
import org.oxycblt.auxio.list.selection.SelectionViewModel
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.navigation.NavigationViewModel
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.logD

class PlaylistListFragment :
    ListFragment<Playlist, FragmentHomeListBinding>(),
    FastScrollRecyclerView.PopupProvider,
    FastScrollRecyclerView.Listener {
    private val homeModel: HomeViewModel by activityViewModels()
    override val navModel: NavigationViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()
    override val selectionModel: SelectionViewModel by activityViewModels()
    private val playlistAdapter = PlaylistAdapter(this)

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentHomeListBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentHomeListBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.homeRecycler.apply {
            id = R.id.home_playlist_recycler
            adapter = playlistAdapter
            popupProvider = this@PlaylistListFragment
            listener = this@PlaylistListFragment
        }

        collectImmediately(homeModel.playlistsList, ::updatePlaylists)
        collectImmediately(selectionModel.selected, ::updateSelection)
        collectImmediately(playbackModel.parent, playbackModel.isPlaying, ::updatePlayback)
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
        val playlist = homeModel.playlistsList.value[pos]
        // Change how we display the popup depending on the current sort mode.
        return when (homeModel.getSortForTab(MusicMode.GENRES).mode) {
            // By Name -> Use Name
            is Sort.Mode.ByName -> playlist.sortName?.thumbString

            // Duration -> Use formatted duration
            is Sort.Mode.ByDuration -> playlist.durationMs.formatDurationMs(false)

            // Count -> Use song count
            is Sort.Mode.ByCount -> playlist.songs.size.toString()

            // Unsupported sort, error gracefully
            else -> null
        }
    }

    override fun onFastScrollingChanged(isFastScrolling: Boolean) {
        homeModel.setFastScrolling(isFastScrolling)
    }

    override fun onRealClick(item: Playlist) {
        navModel.exploreNavigateTo(item)
    }

    override fun onOpenMenu(item: Playlist, anchor: View) {
        openMusicMenu(anchor, R.menu.menu_parent_actions, item)
    }

    private fun updatePlaylists(playlists: List<Playlist>) {
        playlistAdapter.update(
            playlists, homeModel.playlistsInstructions.consume().also { logD(it) })
    }

    private fun updateSelection(selection: List<Music>) {
        playlistAdapter.setSelected(selection.filterIsInstanceTo(mutableSetOf()))
    }

    private fun updatePlayback(parent: MusicParent?, isPlaying: Boolean) {
        // If a playlist is playing, highlight it within this adapter.
        playlistAdapter.setPlaying(parent as? Playlist, isPlaying)
    }

    /**
     * A [SelectionIndicatorAdapter] that shows a list of [Playlist]s using [PlaylistViewHolder].
     *
     * @param listener An [SelectableListListener] to bind interactions to.
     */
    private class PlaylistAdapter(private val listener: SelectableListListener<Playlist>) :
        SelectionIndicatorAdapter<Playlist, PlaylistViewHolder>(PlaylistViewHolder.DIFF_CALLBACK) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            PlaylistViewHolder.from(parent)

        override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
            holder.bind(getItem(position), listener)
        }
    }
}
