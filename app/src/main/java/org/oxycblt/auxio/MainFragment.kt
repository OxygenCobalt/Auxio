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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.oxycblt.auxio.databinding.FragmentMainBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.isLandscape
import org.oxycblt.auxio.util.logD

/**
 * A wrapper around the home fragment that shows the playback fragment and controls
 * the more high-level navigation features.
 * TODO: Re-add the nice playback slide in animation
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

        // --- VIEWMODEL SETUP ---

        playbackModel.setupPlayback(requireContext())

        // Change CompactPlaybackFragment's visibility here so that an animation occurs.
        handleCompactPlaybackVisibility(binding, playbackModel.song.value)

        playbackModel.song.observe(viewLifecycleOwner) { song ->
            handleCompactPlaybackVisibility(binding, song)
        }

        logD("Fragment Created.")

        return binding.root
    }

    /**
     * Handle the visibility of CompactPlaybackFragment. Done here so that there's a nice animation.
     */
    private fun handleCompactPlaybackVisibility(binding: FragmentMainBinding, song: Song?) {
        if (song == null) {
            logD("Hiding CompactPlaybackFragment since no song is being played.")

            binding.mainPlayback.visibility = if (requireContext().isLandscape()) {
                View.INVISIBLE
            } else {
                View.GONE
            }
        } else {
            binding.mainPlayback.visibility = View.VISIBLE
        }
    }
}
