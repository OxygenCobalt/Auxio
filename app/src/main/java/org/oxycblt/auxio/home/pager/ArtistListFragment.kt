/*
 * Copyright (c) 2021 Auxio Project
 * GenreListFragment.kt is part of Auxio.
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

package org.oxycblt.auxio.home.pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.home.HomeAdapter
import org.oxycblt.auxio.home.HomeFragmentDirections
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.newMenu

class ArtistListFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeListBinding.inflate(inflater)

        val adapter = HomeAdapter(
            doOnClick = { item ->
                HomeFragmentDirections.actionShowArtist(item.id)
            },
            ::newMenu
        )

        adapter.updateData(MusicStore.getInstance().artists)

        // --- UI SETUP ---

        binding.homeRecycler.adapter = adapter

        return binding.root
    }
}
