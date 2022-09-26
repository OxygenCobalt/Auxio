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
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentPlaybackBarBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.ui.MainNavigationAction
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.ui.fragment.ViewBindingFragment
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.getColorCompat

/**
 * A fragment showing the current playback state in a compact manner. Used as the bar for the
 * playback sheet.
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
        val context = requireContext()

        binding.root.apply {
            setOnClickListener { navModel.mainNavigateTo(MainNavigationAction.Expand) }

            setOnLongClickListener {
                playbackModel.song.value?.let(navModel::exploreNavigateTo)
                true
            }
        }

        binding.playbackSong.isSelected = true
        binding.playbackInfo.isSelected = true

        // Load the track color in manually as it's unclear whether the track actually supports
        // using a ColorStateList in the resources
        binding.playbackProgressBar.trackColor =
            context.getColorCompat(R.color.sel_track).defaultColor

        binding.playbackPlayPause.setOnClickListener { playbackModel.invertPlaying() }

        // Update the secondary action to match the setting.

        when (Settings(context).actionMode) {
            ActionMode.NEXT -> {
                binding.playbackSecondaryAction.apply {
                    setIconResource(R.drawable.ic_skip_next_24)
                    contentDescription = getString(R.string.desc_skip_next)
                    iconTint = context.getAttrColorCompat(R.attr.colorOnSurfaceVariant)
                    setOnClickListener { playbackModel.next() }
                }
            }
            ActionMode.REPEAT -> {
                binding.playbackSecondaryAction.apply {
                    contentDescription = getString(R.string.desc_change_repeat)
                    iconTint = context.getColorCompat(R.color.sel_accented)
                    setOnClickListener { playbackModel.incrementRepeatMode() }
                    collectImmediately(playbackModel.repeatMode, ::updateRepeat)
                }
            }
            ActionMode.SHUFFLE -> {
                binding.playbackSecondaryAction.apply {
                    setIconResource(R.drawable.sel_shuffle_state_24)
                    contentDescription = getString(R.string.desc_shuffle)
                    iconTint = context.getColorCompat(R.color.sel_accented)
                    setOnClickListener { playbackModel.invertShuffled() }
                    collectImmediately(playbackModel.isShuffled, ::updateShuffled)
                }
            }
        }

        // -- VIEWMODEL SETUP ---

        collectImmediately(playbackModel.song, ::updateSong)
        collectImmediately(playbackModel.isPlaying, ::updateIsPlaying)
        collectImmediately(playbackModel.positionDs, ::updatePosition)
    }

    override fun onDestroyBinding(binding: FragmentPlaybackBarBinding) {
        super.onDestroyBinding(binding)
        binding.playbackSong.isSelected = false
        binding.playbackInfo.isSelected = false
    }

    private fun updateSong(song: Song?) {
        if (song != null) {
            val context = requireContext()
            val binding = requireBinding()
            binding.playbackCover.bind(song)
            binding.playbackSong.text = song.resolveName(context)
            binding.playbackInfo.text = song.resolveArtistContents(context)
            binding.playbackProgressBar.max = song.durationMs.msToDs().toInt()
        }
    }

    private fun updateIsPlaying(isPlaying: Boolean) {
        requireBinding().playbackPlayPause.isActivated = isPlaying
    }

    private fun updateRepeat(repeatMode: RepeatMode) {
        requireBinding().playbackSecondaryAction.apply {
            setIconResource(repeatMode.icon)
            isActivated = repeatMode != RepeatMode.NONE
        }
    }

    private fun updateShuffled(isShuffled: Boolean) {
        requireBinding().playbackSecondaryAction.isActivated = isShuffled
    }

    private fun updatePosition(positionDs: Long) {
        requireBinding().playbackProgressBar.progress = positionDs.toInt()
    }
}
