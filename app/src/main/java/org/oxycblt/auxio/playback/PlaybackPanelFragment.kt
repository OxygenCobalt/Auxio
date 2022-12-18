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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentPlaybackPanelBinding
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.shared.MainNavigationAction
import org.oxycblt.auxio.shared.NavigationViewModel
import org.oxycblt.auxio.shared.ViewBindingFragment
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.systemBarInsetsCompat

/**
 * A [Fragment] that displays more information about the song, along with more media controls.
 * Instantiation is done by the navigation component, **do not instantiate this fragment manually.**
 * @author OxygenCobalt
 *
 * TODO: Make seek thumb grow when selected
 */
class PlaybackPanelFragment : ViewBindingFragment<FragmentPlaybackPanelBinding>() {
    private val playbackModel: PlaybackViewModel by androidActivityViewModels()
    private val navModel: NavigationViewModel by activityViewModels()

    // AudioEffect expects you to use startActivityForResult with the panel intent. Use
    // the contract analogue for this since there is no built-in contract for AudioEffect.
    private val activityLauncher by lifecycleObject {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // Nothing to do
        }
    }

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentPlaybackPanelBinding.inflate(inflater)

    override fun onBindingCreated(
        binding: FragmentPlaybackPanelBinding,
        savedInstanceState: Bundle?
    ) {
        // --- UI SETUP ---

        binding.root.setOnApplyWindowInsetsListener { view, insets ->
            val bars = insets.systemBarInsetsCompat
            view.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

        binding.playbackToolbar.apply {
            setNavigationOnClickListener { navModel.mainNavigateTo(MainNavigationAction.Collapse) }
            setOnMenuItemClickListener {
                handleMenuItem(it)
                true
            }
        }

        // Make sure we enable marquee on the song info

        binding.playbackSong.apply {
            isSelected = true
            setOnClickListener { playbackModel.song.value?.let(navModel::exploreNavigateTo) }
        }

        binding.playbackArtist.apply {
            isSelected = true
            setOnClickListener { playbackModel.song.value?.let { showCurrentArtist() } }
        }

        binding.playbackAlbum.apply {
            isSelected = true
            setOnClickListener { playbackModel.song.value?.let { showCurrentAlbum() } }
        }

        binding.playbackSeekBar.onSeekConfirmed = playbackModel::seekTo

        binding.playbackRepeat.setOnClickListener { playbackModel.incrementRepeatMode() }
        binding.playbackSkipPrev.setOnClickListener { playbackModel.prev() }
        binding.playbackPlayPause.setOnClickListener { playbackModel.invertPlaying() }
        binding.playbackSkipNext.setOnClickListener { playbackModel.next() }
        binding.playbackShuffle.setOnClickListener { playbackModel.invertShuffled() }

        // --- VIEWMODEL SETUP --

        collectImmediately(playbackModel.song, ::updateSong)
        collectImmediately(playbackModel.parent, ::updateParent)
        collectImmediately(playbackModel.positionDs, ::updatePosition)
        collectImmediately(playbackModel.repeatMode, ::updateRepeat)
        collectImmediately(playbackModel.isPlaying, ::updatePlaying)
        collectImmediately(playbackModel.isShuffled, ::updateShuffled)
    }

    override fun onDestroyBinding(binding: FragmentPlaybackPanelBinding) {
        // Leaving marquee on will cause a leak
        binding.playbackSong.isSelected = false
        binding.playbackArtist.isSelected = false
        binding.playbackAlbum.isSelected = false
    }

    private fun handleMenuItem(item: MenuItem) {
        when (item.itemId) {
            R.id.action_open_equalizer -> {
                val equalizerIntent =
                    Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                        .putExtra(
                            AudioEffect.EXTRA_AUDIO_SESSION, playbackModel.currentAudioSessionId)
                        .putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)

                try {
                    activityLauncher.launch(equalizerIntent)
                } catch (e: ActivityNotFoundException) {
                    requireContext().showToast(R.string.err_no_app)
                }
            }
            R.id.action_go_artist -> {
                showCurrentArtist()
            }
            R.id.action_go_album -> {
                showCurrentAlbum()
            }
            R.id.action_song_detail -> {
                playbackModel.song.value?.let { song ->
                    navModel.mainNavigateTo(
                        MainNavigationAction.Directions(
                            MainFragmentDirections.actionShowDetails(song.uid)))
                }
            }
        }
    }

    private fun updateSong(song: Song?) {
        if (song == null) return
        val binding = requireBinding()
        val context = requireContext()
        binding.playbackCover.bind(song)
        binding.playbackSong.text = song.resolveName(context)
        binding.playbackArtist.text = song.resolveArtistContents(context)
        binding.playbackAlbum.text = song.album.resolveName(context)
        binding.playbackSeekBar.durationDs = song.durationMs.msToDs()
    }

    private fun updateParent(parent: MusicParent?) {
        val binding = requireBinding()
        val context = requireContext()
        binding.playbackToolbar.subtitle =
            parent?.resolveName(context) ?: context.getString(R.string.lbl_all_songs)
    }

    private fun updatePosition(positionDs: Long) {
        requireBinding().playbackSeekBar.positionDs = positionDs
    }

    private fun updateRepeat(repeatMode: RepeatMode) {
        requireBinding().playbackRepeat.apply {
            setIconResource(repeatMode.icon)
            isActivated = repeatMode != RepeatMode.NONE
        }
    }

    private fun updatePlaying(isPlaying: Boolean) {
        requireBinding().playbackPlayPause.isActivated = isPlaying
    }

    private fun updateShuffled(isShuffled: Boolean) {
        requireBinding().playbackShuffle.isActivated = isShuffled
    }

    private fun showCurrentArtist() {
        val song = playbackModel.song.value ?: return
        navModel.exploreNavigateTo(song.artists)
    }

    private fun showCurrentAlbum() {
        val song = playbackModel.song.value ?: return
        navModel.exploreNavigateTo(song.album)
    }
}
