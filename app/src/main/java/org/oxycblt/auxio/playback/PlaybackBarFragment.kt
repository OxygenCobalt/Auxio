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

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentPlaybackBarBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.headunit.HeadUnitUiAdapter
import org.oxycblt.auxio.music.resolve
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.playback.state.ShuffleScope
import org.oxycblt.auxio.ui.UISettings
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.showToast
import org.oxycblt.musikr.Song

/**
 * A [ViewBindingFragment] that shows the current playback state in a compact manner.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class PlaybackBarFragment : ViewBindingFragment<FragmentPlaybackBarBinding>() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    @Inject lateinit var uiSettings: UISettings

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentPlaybackBarBinding.inflate(inflater)

    override fun onBindingCreated(
        binding: FragmentPlaybackBarBinding,
        savedInstanceState: Bundle?,
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
        binding.playbackRepeat.setOnClickListener { playbackModel.toggleRepeatMode() }
        binding.playbackSkipPrev.setOnClickListener {
            playbackModel.prev()
            context.showToast(R.string.msg_playback_previous)
        }
        binding.playbackPlayPause.setOnClickListener {
            playbackModel.togglePlaying()
            context.showToast(
                if (playbackModel.isPlaying.value) R.string.msg_playback_play
                else R.string.msg_playback_pause
            )
        }
        binding.playbackSkipNext.setOnClickListener {
            playbackModel.next()
            context.showToast(R.string.msg_playback_next)
        }
        binding.playbackShuffle.setOnClickListener { playbackModel.cycleShuffleScope() }

        val useLargeControls =
            uiSettings.largeHeadUnitControls ||
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        HeadUnitUiAdapter.applyUniformMediaControls(
            resources,
            useLargeControls,
            listOf(
                binding.playbackRepeat,
                binding.playbackSkipPrev,
                binding.playbackPlayPause,
                binding.playbackSkipNext,
                binding.playbackShuffle,
            ),
            compact = true,
            primaryButton = binding.playbackPlayPause,
        )
        HeadUnitUiAdapter.applyLargePlaybackText(
            resources,
            useLargeControls,
            binding.playbackSong,
            binding.playbackInfo,
        )
        applyDriverSideLayout(binding)

        // -- VIEWMODEL SETUP ---
        collectImmediately(playbackModel.song, ::updateSong)
        collectImmediately(playbackModel.isPlaying, ::updatePlaying)
        collectImmediately(playbackModel.positionDs, ::updatePosition)
        collectImmediately(playbackModel.repeatMode, ::updateRepeat)
        collectImmediately(playbackModel.shuffleScope, ::updateShuffleScope)
    }

    override fun onDestroyBinding(binding: FragmentPlaybackBarBinding) {
        super.onDestroyBinding(binding)
        binding.playbackRepeat.clearPendingIcon()
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

    private fun updatePosition(positionDs: Long) {
        requireBinding().playbackProgressBar.progress = positionDs.toInt()
    }

    private fun updateRepeat(repeatMode: RepeatMode) {
        requireBinding().playbackRepeat.apply {
            isChecked = repeatMode != RepeatMode.NONE
            setIconResource(repeatMode.icon)
        }
    }

    private fun updateShuffleScope(scope: ShuffleScope) {
        requireBinding().playbackShuffle.apply {
            when (scope) {
                ShuffleScope.OFF -> {
                    isChecked = false
                    setIconResource(R.drawable.sel_shuffle_state_24)
                    contentDescription = context.getString(R.string.desc_shuffle_off)
                }
                ShuffleScope.ALL -> {
                    isChecked = true
                    setIconResource(R.drawable.sel_shuffle_state_24)
                    contentDescription = context.getString(R.string.desc_shuffle_all_songs)
                }
                ShuffleScope.GENRE -> {
                    isChecked = true
                    setIconResource(R.drawable.ic_shuffle_genre_state_24)
                    contentDescription = context.getString(R.string.desc_shuffle_current_genre)
                }
            }
        }
    }

    private fun applyDriverSideLayout(binding: FragmentPlaybackBarBinding) {
        if (uiSettings.driverSide != UISettings.DriverSide.LEFT) {
            return
        }
        val root = binding.root as ConstraintLayout
        ConstraintSet().apply {
            clone(root)
            clear(R.id.playback_cover, ConstraintSet.START)
            connect(
                R.id.playback_cover,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
            )

            clear(R.id.playback_controls_wrapper, ConstraintSet.END)
            connect(
                R.id.playback_controls_wrapper,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
            )

            clear(R.id.playback_song, ConstraintSet.START)
            clear(R.id.playback_song, ConstraintSet.END)
            connect(
                R.id.playback_song,
                ConstraintSet.START,
                R.id.playback_controls_wrapper,
                ConstraintSet.END,
            )
            connect(R.id.playback_song, ConstraintSet.END, R.id.playback_cover, ConstraintSet.START)

            clear(R.id.playback_info, ConstraintSet.START)
            clear(R.id.playback_info, ConstraintSet.END)
            connect(
                R.id.playback_info,
                ConstraintSet.START,
                R.id.playback_controls_wrapper,
                ConstraintSet.END,
            )
            connect(R.id.playback_info, ConstraintSet.END, R.id.playback_cover, ConstraintSet.START)
            applyTo(root)
        }
    }
}
