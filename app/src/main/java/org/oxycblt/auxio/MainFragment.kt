/*
 * Copyright (c) 2021 Auxio Project
 * MainFragment.kt is part of Auxio.
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

package org.oxycblt.auxio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.oxycblt.auxio.databinding.FragmentMainBinding
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.applyEdge
import org.oxycblt.auxio.util.applyMaterialDrawable
import org.oxycblt.auxio.util.logD

/**
 * A wrapper around the home fragment that shows the playback fragment and controls
 * the more high-level navigation features.
 */
class MainFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner

        binding.applyEdge { bars ->
            binding.mainPlayback.updatePadding(bottom = bars.bottom)
        }

        binding.mainPlayback.applyMaterialDrawable()

        // --- VIEWMODEL SETUP ---

        playbackModel.setupPlayback(requireContext())

        // Change CompactPlaybackFragment's visibility here so that an animation occurs.
        binding.mainPlayback.isVisible = playbackModel.song.value != null

        playbackModel.song.observe(viewLifecycleOwner) { song ->
            binding.mainPlayback.isVisible = song != null
        }

        logD("Fragment Created.")

        return binding.root
    }
}
