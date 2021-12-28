/*
 * Copyright (c) 2021 Auxio Project
 * PlaybackFragment.kt is part of Auxio.
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
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentPlaybackBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.ui.memberBinding
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.systemBarsCompat

/**
 * A [Fragment] that displays more information about the song, along with more media controls.
 * Instantiation is done by the navigation component, **do not instantiate this fragment manually.**
 * @author OxygenCobalt
 */
class PlaybackFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    private val binding by memberBinding(FragmentPlaybackBinding::inflate) {
        playbackSong.isSelected = false // Clear marquee to prevent a memory leak
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val queueItem: MenuItem

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner
        binding.playbackModel = playbackModel
        binding.detailModel = detailModel

        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val bars = insets.systemBarsCompat

            binding.root.updatePadding(
                top = bars.top,
                bottom = bars.bottom
            )

            insets
        }

        binding.playbackToolbar.apply {
            setNavigationOnClickListener {
                navigateUp()
            }

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

        // Make marquee of song title work
        binding.playbackSong.isSelected = true
        binding.playbackSeekBar.onConfirmListener = playbackModel::setPosition

        binding.playbackPlayPause.post {
            binding.playbackPlayPause.stateListAnimator = null
        }

        // --- VIEWMODEL SETUP --

        playbackModel.song.observe(viewLifecycleOwner) { song ->
            if (song != null) {
                logD("Updating song display to ${song.name}.")

                binding.song = song
                binding.playbackSeekBar.setDuration(song.seconds)
            } else {
                logD("No song is being played, leaving.")

                findNavController().navigateUp()
            }
        }

        playbackModel.isShuffling.observe(viewLifecycleOwner) { isShuffling ->
            binding.playbackShuffle.isActivated = isShuffling
        }

        playbackModel.loopMode.observe(viewLifecycleOwner) { loopMode ->
            val resId = when (loopMode) {
                LoopMode.NONE, null -> R.drawable.ic_loop
                LoopMode.ALL -> R.drawable.ic_loop_on
                LoopMode.TRACK -> R.drawable.ic_loop_one
            }

            binding.playbackLoop.setImageResource(resId)
        }

        playbackModel.position.observe(viewLifecycleOwner) { pos ->
            binding.playbackSeekBar.setProgress(pos)
        }

        playbackModel.nextUp.observe(viewLifecycleOwner) {
            // The queue icon uses a selector that will automatically tint the icon as active or
            // inactive. We just need to set the flag.
            queueItem.isEnabled = playbackModel.nextUp.value!!.isNotEmpty()
        }

        playbackModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.playbackPlayPause.isActivated = isPlaying
        }

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            if (item != null) {
                navigateUp()
            }
        }

        logD("Fragment Created.")

        return binding.root
    }

    private fun navigateUp() {
        // This is a dumb and fragile hack but this fragment isn't part of the navigation stack
        // so we can't really do much
        (requireView().parent.parent.parent as PlaybackLayout).collapse()
    }
}
