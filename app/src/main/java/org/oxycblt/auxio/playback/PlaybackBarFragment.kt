/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.playback

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import kotlin.math.max
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentPlaybackBarBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.MainNavigationAction
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.ui.fragment.ViewBindingFragment
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.getColorStateListSafe
import org.oxycblt.auxio.util.textSafe

/**
 * A fragment showing the current playback state in a compact manner. Placed at the bottom of the
 * screen. This expands into [PlaybackPanelFragment].
 * @author OxygenCobalt
 */
class PlaybackBarFragment : ViewBindingFragment<FragmentPlaybackBarBinding>() {
    private val playbackModel: PlaybackViewModel by androidActivityViewModels()
    private val navModel: NavigationViewModel by activityViewModels()

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentPlaybackBarBinding.inflate(inflater)

    override fun onBindingCreated(
        binding: FragmentPlaybackBarBinding,
        savedInstanceState: Bundle?
    ) {
        binding.root.apply {
            setOnClickListener { navModel.mainNavigateTo(MainNavigationAction.Expand) }

            setOnLongClickListener {
                playbackModel.song.value?.let(navModel::exploreNavigateTo)
                true
            }
        }

        // Load the track color in manually as it's unclear whether the track actually supports
        // using a ColorStateList in the resources
        binding.playbackProgressBar.trackColor =
            requireContext().getColorStateListSafe(R.color.sel_track).defaultColor

        binding.playbackPlayPause.setOnClickListener { playbackModel.invertPlaying() }

        binding.playbackSkipNext.setOnClickListener { playbackModel.next() }

        // -- VIEWMODEL SETUP ---

        collectImmediately(playbackModel.song, ::updateSong)
        collectImmediately(playbackModel.isPlaying, ::updateIsPlaying)
        collectImmediately(playbackModel.positionSecs, ::updatePosition)
    }

    private fun updateSong(song: Song?) {
        if (song != null) {
            val context = requireContext()
            val binding = requireBinding()
            binding.playbackCover.bind(song)
            binding.playbackSong.textSafe = song.resolveName(context)
            binding.playbackInfo.textSafe = song.resolveIndividualArtistName(context)
            binding.playbackProgressBar.max = song.durationSecs.toInt()
        }
    }

    private fun updateIsPlaying(isPlaying: Boolean) {
        requireBinding().playbackPlayPause.isActivated = isPlaying
    }

    private fun updatePosition(position: Long) {
        requireBinding().playbackProgressBar.progress = position.toInt()
    }
}
