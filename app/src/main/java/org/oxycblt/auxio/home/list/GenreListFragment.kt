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
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.home.HomeViewModel
import org.oxycblt.auxio.home.fastscroll.FastScrollRecyclerView
import org.oxycblt.auxio.list.*
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.Sort
import org.oxycblt.auxio.list.adapter.BasicListInstructions
import org.oxycblt.auxio.list.adapter.ListDiffer
import org.oxycblt.auxio.list.adapter.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.recycler.GenreViewHolder
import org.oxycblt.auxio.list.selection.SelectionViewModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.util.collectImmediately

/**
 * A [ListFragment] that shows a list of [Genre]s.
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class GenreListFragment :
    ListFragment<Genre, FragmentHomeListBinding>(),
    FastScrollRecyclerView.PopupProvider,
    FastScrollRecyclerView.Listener {
    private val homeModel: HomeViewModel by activityViewModels()
    override val navModel: NavigationViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()
    override val selectionModel: SelectionViewModel by activityViewModels()
    private val genreAdapter = GenreAdapter(this)

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentHomeListBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentHomeListBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.homeRecycler.apply {
            id = R.id.home_genre_recycler
            adapter = genreAdapter
            popupProvider = this@GenreListFragment
            listener = this@GenreListFragment
        }

        collectImmediately(homeModel.genresList, ::updateList)
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
        val genre = homeModel.genresList.value[pos]
        // Change how we display the popup depending on the current sort mode.
        return when (homeModel.getSortForTab(MusicMode.GENRES).mode) {
            // By Name -> Use Name
            is Sort.Mode.ByName -> genre.collationKey?.run { sourceString.first().uppercase() }

            // Duration -> Use formatted duration
            is Sort.Mode.ByDuration -> genre.durationMs.formatDurationMs(false)

            // Count -> Use song count
            is Sort.Mode.ByCount -> genre.songs.size.toString()

            // Unsupported sort, error gracefully
            else -> null
        }
    }

    override fun onFastScrollingChanged(isFastScrolling: Boolean) {
        homeModel.setFastScrolling(isFastScrolling)
    }

    override fun onRealClick(item: Genre) {
        navModel.exploreNavigateTo(item)
    }

    override fun onOpenMenu(item: Genre, anchor: View) {
        openMusicMenu(anchor, R.menu.menu_artist_actions, item)
    }

    private fun updateList(artists: List<Genre>) {
        genreAdapter.submitList(artists, BasicListInstructions.REPLACE)
    }

    private fun updateSelection(selection: List<Music>) {
        genreAdapter.setSelected(selection.filterIsInstanceTo(mutableSetOf()))
    }

    private fun updatePlayback(parent: MusicParent?, isPlaying: Boolean) {
        // If a genre is playing, highlight it within this adapter.
        genreAdapter.setPlaying(parent as? Genre, isPlaying)
    }

    /**
     * A [SelectionIndicatorAdapter] that shows a list of [Genre]s using [GenreViewHolder].
     * @param listener An [SelectableListListener] to bind interactions to.
     */
    private class GenreAdapter(private val listener: SelectableListListener<Genre>) :
        SelectionIndicatorAdapter<Genre, BasicListInstructions, GenreViewHolder>(
            ListDiffer.Blocking(GenreViewHolder.DIFF_CALLBACK)) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            GenreViewHolder.from(parent)

        override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
            holder.bind(getItem(position), listener)
        }
    }
}
