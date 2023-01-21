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
import org.oxycblt.auxio.home.fastscroll.FastScrollRecyclerView
import org.oxycblt.auxio.list.*
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.adapter.BasicListInstructions
import org.oxycblt.auxio.list.adapter.ListDiffer
import org.oxycblt.auxio.list.adapter.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.recycler.ArtistViewHolder
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.library.Sort
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.nonZeroOrNull

/**
 * A [ListFragment] that shows a list of [Artist]s.
 * @author Alexander Capehart (OxygenCobalt)
 */
class ArtistListFragment :
    ListFragment<Artist, FragmentHomeListBinding>(),
    FastScrollRecyclerView.PopupProvider,
    FastScrollRecyclerView.Listener {
    private val homeModel: HomeViewModel by activityViewModels()
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

        collectImmediately(homeModel.artistsList, ::updateList)
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
        val artist = homeModel.artistsList.value[pos]
        // Change how we display the popup depending on the current sort mode.
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

    override fun onFastScrollingChanged(isFastScrolling: Boolean) {
        homeModel.setFastScrolling(isFastScrolling)
    }

    override fun onRealClick(item: Artist) {
        navModel.exploreNavigateTo(item)
    }

    override fun onOpenMenu(item: Artist, anchor: View) {
        openMusicMenu(anchor, R.menu.menu_artist_actions, item)
    }

    private fun updateList(artists: List<Artist>) {
        artistAdapter.submitList(artists, BasicListInstructions.REPLACE)
    }

    private fun updateSelection(selection: List<Music>) {
        artistAdapter.setSelected(selection.filterIsInstanceTo(mutableSetOf()))
    }

    private fun updatePlayback(parent: MusicParent?, isPlaying: Boolean) {
        // If an artist is playing, highlight it within this adapter.
        artistAdapter.setPlaying(parent as? Artist, isPlaying)
    }

    /**
     * A [SelectionIndicatorAdapter] that shows a list of [Artist]s using [ArtistViewHolder].
     * @param listener An [SelectableListListener] to bind interactions to.
     */
    private class ArtistAdapter(private val listener: SelectableListListener<Artist>) :
        SelectionIndicatorAdapter<Artist, BasicListInstructions, ArtistViewHolder>(
            ListDiffer.Blocking(ArtistViewHolder.DIFF_CALLBACK)) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ArtistViewHolder.from(parent)

        override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
            holder.bind(getItem(position), listener)
        }
    }
}
