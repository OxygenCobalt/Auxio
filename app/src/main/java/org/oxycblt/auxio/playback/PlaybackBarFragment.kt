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

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.ostrichbat2k.usbdac.UsbAudioManager
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentPlaybackBarBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.resolve
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.musikr.Song
import timber.log.Timber as L

/**
 * A [ViewBindingFragment] that shows the current playback state in a compact manner.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class PlaybackBarFragment : ViewBindingFragment<FragmentPlaybackBarBinding>() {
    @Inject lateinit var usbAudioManager: UsbAudioManager
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentPlaybackBarBinding.inflate(inflater)

    override fun onBindingCreated(
        binding: FragmentPlaybackBarBinding,
        savedInstanceState: Bundle?,
    ) {
        super.onBindingCreated(binding, savedInstanceState)

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

        // -- VIEWMODEL SETUP ---
        collectImmediately(playbackModel.song, ::updateSong)
        collectImmediately(playbackModel.isPlaying, ::updatePlaying)
        collectImmediately(playbackModel.positionDs, ::updatePosition)
        collectImmediately(
            playbackModel.currentBarAction,
            playbackModel.repeatMode,
            playbackModel.isShuffled,
            ::updateBarAction,
        )
        collectImmediately(usbAudioManager.blockPlayback, ::updateBlockPlayback)
    }

    override fun onDestroyBinding(binding: FragmentPlaybackBarBinding) {
        super.onDestroyBinding(binding)
        binding.playbackSecondaryAction.clearPendingIcon()
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
        requireBinding().playbackPlayPause.isChecked = isPlaying
    }

    private var dacActivatingSpin: ObjectAnimator? = null

    /**
     * See [UsbAudioManager.blockPlayback]'s doc comment: disables this button and swaps its icon
     * for a spinner while a USB DAC activation this control would otherwise race is in flight,
     * instead of letting a tap start playback that might land on HAL purely due to timing.
     *
     * See [org.oxycblt.auxio.playback.PlaybackPanelFragment.updateBlockPlayback]'s doc comment for
     * why this spins the button view itself via [ObjectAnimator] rather than animating the icon
     * drawable -- same button style, same confirmed failure either way.
     */
    private fun updateBlockPlayback(blocked: Boolean) {
        val button = requireBinding().playbackPlayPause
        button.isEnabled = !blocked
        if (blocked) {
            button.setIconResource(R.drawable.ic_dac_activating_24)
            dacActivatingSpin?.cancel()
            dacActivatingSpin =
                ObjectAnimator.ofFloat(button, View.ROTATION, 0f, 360f).apply {
                    duration = 900
                    repeatCount = ObjectAnimator.INFINITE
                    interpolator = LinearInterpolator()
                    start()
                }
        } else {
            dacActivatingSpin?.cancel()
            dacActivatingSpin = null
            button.rotation = 0f
            button.setIconResource(R.drawable.sel_playing_state_24)
        }
    }

    private fun updatePosition(positionDs: Long) {
        requireBinding().playbackProgressBar.progress = positionDs.toInt()
    }

    private fun updateBarAction(
        actionMode: ActionMode,
        repeatMode: RepeatMode,
        isShuffled: Boolean,
    ) {
        val binding = requireBinding()
        when (actionMode) {
            ActionMode.NEXT -> {
                L.d("Using skip next action")
                binding.playbackSecondaryAction.apply {
                    if (tag != actionMode) {
                        setIconResource(R.drawable.ic_skip_next_24)
                        contentDescription = getString(R.string.desc_skip_next)
                        setOnClickListener { playbackModel.next() }
                        isChecked = false
                        tag = actionMode
                    }
                }
            }
            ActionMode.REPEAT -> {
                L.d("Using repeat mode action")
                binding.playbackSecondaryAction.apply {
                    if (tag != actionMode) {
                        contentDescription = getString(R.string.desc_change_repeat)
                        setOnClickListener { playbackModel.toggleRepeatMode() }
                        tag = actionMode
                    }
                    setIconResource(repeatMode.icon)
                    isChecked = repeatMode != RepeatMode.NONE
                }
            }
            ActionMode.SHUFFLE -> {
                L.d("Using shuffle action")
                binding.playbackSecondaryAction.apply {
                    if (tag != actionMode) {
                        setIconResource(R.drawable.sel_shuffle_state_24)
                        contentDescription = getString(R.string.desc_shuffle)
                        setOnClickListener { playbackModel.toggleShuffled() }
                        tag = actionMode
                    }
                    isChecked = isShuffled
                }
            }
        }
    }
}
