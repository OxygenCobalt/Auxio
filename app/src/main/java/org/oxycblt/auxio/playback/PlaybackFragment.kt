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
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.systemBarInsetsCompat

/**
 * A [Fragment] that displays more information about the song, along with more media controls.
 * Instantiation is done by the navigation component, **do not instantiate this fragment manually.**
 * @author OxygenCobalt
 *
 * TODO: Handle RTL correctly in the playback buttons
 */
class PlaybackFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    private var mLastBinding: FragmentPlaybackBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPlaybackBinding.inflate(layoutInflater)
        val queueItem: MenuItem

        // See onDestroyView for why we do this
        mLastBinding = binding

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner
        binding.playbackModel = playbackModel
        binding.detailModel = detailModel

        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val bars = insets.systemBarInsetsCompat
            binding.root.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

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

        // Make marquee of song title work
        binding.playbackSong.isSelected = true
        binding.playbackSeekBar.onConfirmListener = playbackModel::setPosition

        // Abuse the play/pause FAB (see style definition for more info)
        binding.playbackPlayPause.post { binding.playbackPlayPause.stateListAnimator = null }

        // --- VIEWMODEL SETUP --

        playbackModel.song.observe(viewLifecycleOwner) { song ->
            if (song != null) {
                logD("Updating song display to ${song.rawName}")
                binding.song = song
                binding.playbackSeekBar.setDuration(song.seconds)
            } else {
                logD("No song is being played, leaving")
                findNavController().navigateUp()
            }
        }

        playbackModel.parent.observe(viewLifecycleOwner) { parent ->
            binding.playbackToolbar.subtitle =
                parent?.resolvedName ?: getString(R.string.lbl_all_songs)
        }

        playbackModel.isShuffling.observe(viewLifecycleOwner) { isShuffling ->
            binding.playbackShuffle.isActivated = isShuffling
        }

        playbackModel.loopMode.observe(viewLifecycleOwner) { loopMode ->
            val resId =
                when (loopMode) {
                    LoopMode.NONE, null -> R.drawable.ic_loop
                    LoopMode.ALL -> R.drawable.ic_loop_on
                    LoopMode.TRACK -> R.drawable.ic_loop_one
                }

            binding.playbackLoop.apply {
                isActivated = loopMode != LoopMode.NONE
                setImageResource(resId)
            }
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

        logD("Fragment Created")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // playbackSong will leak if we don't disable marquee, keep the binding around
        // so that we can turn it off when we destroy the view.
        mLastBinding?.playbackSong?.isSelected = false
        mLastBinding = null
    }

    private fun navigateUp() {
        // This is a dumb and fragile hack but this fragment isn't part of the navigation stack
        // so we can't really do much
        (requireView().parent.parent.parent as PlaybackLayout).collapse()
    }
}
