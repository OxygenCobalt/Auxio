/*
 * Copyright (c) 2022 Auxio Project
 * PlaybackBarFragment.kt is part of Auxio.
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
import com.google.android.material.R as MR
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentPlaybackBarBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.getColorCompat
import org.oxycblt.auxio.util.logD

/**
 * A [ViewBindingFragment] that shows the current playback state in a compact manner.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class PlaybackBarFragment : ViewBindingFragment<FragmentPlaybackBarBinding>() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentPlaybackBarBinding.inflate(inflater)

    override fun onBindingCreated(
        binding: FragmentPlaybackBarBinding,
        savedInstanceState: Bundle?
    ) {
        super.onBindingCreated(binding, savedInstanceState)
        val context = requireContext()

        // --- UI SETUP ---
        binding.root.apply {
            setOnClickListener { playbackModel.openPlayback() }
            setOnLongClickListener {
                playbackModel.song.value?.let(detailModel::showAlbum)
                true
            }
        }

        // Set up marquee on song information
        binding.playbackSong.isSelected = true
        binding.playbackInfo.isSelected = true

        // Set up actions
        binding.playbackPlayPause.setOnClickListener { playbackModel.togglePlaying() }

        // Load the track color in manually as it's unclear whether the track actually supports
        // using a ColorStateList in the resources.
        binding.playbackProgressBar.trackColor =
            context.getColorCompat(R.color.sel_track).defaultColor

        //        binding.playbackProgressBar.wavelength = 48
        //        binding.playbackProgressBar.speed = 20
        //        binding.playbackProgressBar.amplitude = 5

        // -- VIEWMODEL SETUP ---
        collectImmediately(playbackModel.song, ::updateSong)
        collectImmediately(playbackModel.isPlaying, ::updatePlaying)
        collectImmediately(playbackModel.positionDs, ::updatePosition)
        collectImmediately(
            playbackModel.currentBarAction,
            playbackModel.repeatMode,
            playbackModel.isShuffled,
            ::updateBarAction)
    }

    override fun onDestroyBinding(binding: FragmentPlaybackBarBinding) {
        super.onDestroyBinding(binding)
        // Marquee elements leak if they are not disabled when the views are destroyed.
        binding.playbackSong.isSelected = false
        binding.playbackInfo.isSelected = false
    }

    private fun updateSong(song: Song?) {
        if (song == null) {
            // Nothing to do.
            return
        }

        val context = requireContext()
        val binding = requireBinding()
        binding.playbackCover.bind(song)
        binding.playbackSong.text = song.name.resolve(context)
        binding.playbackInfo.text = song.artists.resolveNames(context)
        binding.playbackProgressBar.max = song.durationMs.msToDs().toInt()
    }

    private fun updatePlaying(isPlaying: Boolean) {
        requireBinding().playbackPlayPause.isActivated = isPlaying
    }

    private fun updatePosition(positionDs: Long) {
        requireBinding().playbackProgressBar.progress = positionDs.toInt()
    }

    private fun updateBarAction(
        actionMode: ActionMode,
        repeatMode: RepeatMode,
        isShuffled: Boolean
    ) {
        val binding = requireBinding()
        when (actionMode) {
            ActionMode.NEXT -> {
                logD("Using skip next action")
                binding.playbackSecondaryAction.apply {
                    if (tag != actionMode) {
                        setIconResource(R.drawable.ic_skip_next_24)
                        contentDescription = getString(R.string.desc_skip_next)
                        iconTint = context.getAttrColorCompat(MR.attr.colorOnSurfaceVariant)
                        setOnClickListener { playbackModel.next() }
                        tag = actionMode
                    }
                }
            }
            ActionMode.REPEAT -> {
                logD("Using repeat mode action")
                binding.playbackSecondaryAction.apply {
                    if (tag != actionMode) {
                        contentDescription = getString(R.string.desc_change_repeat)
                        iconTint = context.getColorCompat(R.color.sel_activatable_icon)
                        setOnClickListener { playbackModel.toggleRepeatMode() }
                        tag = actionMode
                    }
                    setIconResource(repeatMode.icon)
                    isActivated = repeatMode != RepeatMode.NONE
                }
            }
            ActionMode.SHUFFLE -> {
                logD("Using shuffle action")
                binding.playbackSecondaryAction.apply {
                    if (tag != actionMode) {
                        setIconResource(R.drawable.sel_shuffle_state_24)
                        contentDescription = getString(R.string.desc_shuffle)
                        iconTint = context.getColorCompat(R.color.sel_activatable_icon)
                        setOnClickListener { playbackModel.toggleShuffled() }
                        tag = actionMode
                    }
                    isActivated = isShuffled
                }
            }
        }
    }
}
