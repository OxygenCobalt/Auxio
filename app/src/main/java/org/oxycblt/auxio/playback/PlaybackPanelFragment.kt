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
 
package org.oxycblt.auxio.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.MaterialColors
import com.google.android.material.slider.Slider
import kotlin.math.max
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.R
import org.oxycblt.auxio.coil.applyAlbumCover
import org.oxycblt.auxio.databinding.FragmentPlaybackPanelBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toDuration
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.ui.BottomSheetLayout
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.getAttrColorSafe
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.stateList
import org.oxycblt.auxio.util.systemBarInsetsCompat
import org.oxycblt.auxio.util.textSafe

/**
 * A [Fragment] that displays more information about the song, along with more media controls.
 * Instantiation is done by the navigation component, **do not instantiate this fragment manually.**
 * @author OxygenCobalt
 *
 * TODO: Handle RTL correctly in the playback buttons
 */
class PlaybackPanelFragment :
    ViewBindingFragment<FragmentPlaybackPanelBinding>(),
    Slider.OnChangeListener,
    Slider.OnSliderTouchListener {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentPlaybackPanelBinding.inflate(inflater)

    override fun onBindingCreated(
        binding: FragmentPlaybackPanelBinding,
        savedInstanceState: Bundle?
    ) {
        // --- UI SETUP ---
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val bars = insets.systemBarInsetsCompat
            binding.root.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

        val queueItem: MenuItem

        binding.playbackToolbar.apply {
            setNavigationOnClickListener { navigateUp() }

            setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.action_queue) {
                    findNavController().navigate(MainFragmentDirections.actionShowQueue())
                    true
                } else {
                    false
                }
            }

            queueItem = menu.findItem(R.id.action_queue)
        }

        binding.playbackSong.apply {
            // Make marquee of the song title work
            isSelected = true
            setOnClickListener { playbackModel.song.value?.let { detailModel.navToItem(it) } }
        }

        binding.playbackArtist.setOnClickListener {
            playbackModel.song.value?.let { detailModel.navToItem(it.album.artist) }
        }

        binding.playbackAlbum.setOnClickListener {
            playbackModel.song.value?.let { detailModel.navToItem(it.album) }
        }

        binding.playbackSeekBar.apply {
            addOnChangeListener(this@PlaybackPanelFragment)
            addOnSliderTouchListener(this@PlaybackPanelFragment)

            // Composite a tint list based on the active/inactive colors
            trackInactiveTintList =
                MaterialColors.compositeARGBWithAlpha(
                        context.getAttrColorSafe(R.attr.colorSecondary), (255 * 0.2).toInt())
                    .stateList
        }

        binding.playbackLoop.setOnClickListener { playbackModel.incrementLoopStatus() }
        binding.playbackSkipPrev.setOnClickListener { playbackModel.skipPrev() }

        binding.playbackPlayPause.apply {
            // Abuse the play/pause FAB (see style definition for more info)
            post { binding.playbackPlayPause.stateListAnimator = null }
            setOnClickListener { playbackModel.invertPlayingStatus() }
        }

        binding.playbackSkipNext.setOnClickListener { playbackModel.skipNext() }
        binding.playbackShuffle.setOnClickListener { playbackModel.invertShuffleStatus() }

        // --- VIEWMODEL SETUP --

        playbackModel.song.observe(viewLifecycleOwner, ::updateSong)
        playbackModel.parent.observe(viewLifecycleOwner, ::updateParent)
        playbackModel.positionSeconds.observe(viewLifecycleOwner, ::updatePosition)
        playbackModel.loopMode.observe(viewLifecycleOwner, ::updateLoop)
        playbackModel.isPlaying.observe(viewLifecycleOwner, ::updatePlayPause)
        playbackModel.isShuffling.observe(viewLifecycleOwner, ::updateShuffle)

        playbackModel.nextUp.observe(viewLifecycleOwner) { nextUp ->
            // The queue icon uses a selector that will automatically tint the icon as active or
            // inactive. We just need to set the flag.
            queueItem.isEnabled = nextUp.isNotEmpty()
        }

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            if (item != null) {
                navigateUp()
            }
        }

        logD("Fragment Created")
    }

    override fun onDestroyBinding(binding: FragmentPlaybackPanelBinding) {
        binding.playbackSong.isSelected = false
        binding.playbackSeekBar.removeOnChangeListener(this)
        binding.playbackSeekBar.removeOnChangeListener(this)
    }

    override fun onStartTrackingTouch(slider: Slider) {
        requireBinding().playbackPosition.isActivated = true
    }

    override fun onStopTrackingTouch(slider: Slider) {
        requireBinding().playbackPosition.isActivated = false
        playbackModel.setPosition(slider.value.toLong())
    }

    override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
        if (fromUser) {
            requireBinding().playbackPosition.textSafe = value.toLong().toDuration(true)
        }
    }

    private fun updateSong(song: Song?) {
        if (song == null) return

        val binding = requireBinding()
        binding.playbackCover.applyAlbumCover(song)
        binding.playbackSong.textSafe = song.resolvedName
        binding.playbackArtist.textSafe = song.resolvedArtistName
        binding.playbackAlbum.textSafe = song.resolvedAlbumName

        // Normally if a song had a duration
        val seconds = song.seconds
        binding.playbackDuration.textSafe = seconds.toDuration(false)
        binding.playbackSeekBar.apply {
            valueTo = max(seconds, 1L).toFloat()
            isEnabled = seconds > 0L
        }
    }

    private fun updateParent(parent: MusicParent?) {
        requireBinding().playbackToolbar.subtitle =
            parent?.resolvedName ?: getString(R.string.lbl_all_songs)
    }

    private fun updatePosition(position: Long) {
        // Don't update the progress while we are seeking, that will make the SeekBar jump
        // around.
        val binding = requireBinding()
        if (!binding.playbackPosition.isActivated) {
            binding.playbackSeekBar.value = position.toFloat()
            binding.playbackPosition.textSafe = position.toDuration(true)
        }
    }

    private fun updateLoop(loopMode: LoopMode) {
        requireBinding().playbackLoop.apply {
            isActivated = loopMode != LoopMode.NONE
            setImageResource(loopMode.icon)
        }
    }

    private fun updatePlayPause(isPlaying: Boolean) {
        requireBinding().playbackPlayPause.isActivated = isPlaying
    }

    private fun updateShuffle(isShuffling: Boolean) {
        requireBinding().playbackShuffle.isActivated = isShuffling
    }

    private fun navigateUp() {
        // This is a dumb and fragile hack but this fragment isn't part of the navigation stack
        // so we can't really do much
        (requireView().parent.parent.parent as BottomSheetLayout).collapse()
    }
}
