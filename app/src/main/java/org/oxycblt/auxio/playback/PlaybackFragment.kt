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
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.accent.Accent
import org.oxycblt.auxio.databinding.FragmentPlaybackBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.resolveDrawable
import org.oxycblt.auxio.resolveStateList
import org.oxycblt.auxio.ui.memberBinding

/**
 * A [Fragment] that displays more information about the song, along with more media controls.
 * Instantiation is done by the navigation component, **do not instantiate this fragment manually.**
 * @author OxygenCobalt
 */
class PlaybackFragment : Fragment(), SeekBar.OnSeekBarChangeListener {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    private val binding by memberBinding(FragmentPlaybackBinding::inflate) {
        playbackSong.isSelected = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val normalTextColor = binding.playbackDurationCurrent.currentTextColor
        val accentColor = Accent.get().getStateList(requireContext())
        val controlColor = R.color.control.resolveStateList(requireContext())

        // Can't set the tint of a MenuItem below Android 8, so use icons instead.
        val iconQueueActive = R.drawable.ic_queue.resolveDrawable(requireContext())
        val iconQueueInactive = R.drawable.ic_queue_inactive.resolveDrawable(requireContext())

        val queueItem: MenuItem

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner
        binding.playbackModel = playbackModel
        binding.detailModel = detailModel

        binding.playbackToolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.action_queue) {
                    findNavController().navigate(PlaybackFragmentDirections.actionShowQueue())

                    true
                } else {
                    false
                }
            }

            queueItem = menu.findItem(R.id.action_queue)
        }

        // Make marquee of song title work
        binding.playbackSong.isSelected = true
        binding.playbackSeekBar.setOnSeekBarChangeListener(this)

        // --- VIEWMODEL SETUP --
        playbackModel.song.observe(viewLifecycleOwner) { song ->
            if (song != null) {
                logD("Updating song display to ${song.name}.")

                binding.song = song
                binding.playbackSeekBar.max = song.seconds.toInt()
            } else {
                logD("No song is being played, leaving.")

                findNavController().navigateUp()
            }
        }

        playbackModel.isShuffling.observe(viewLifecycleOwner) { isShuffling ->
            binding.playbackShuffle.imageTintList = if (isShuffling) {
                accentColor
            } else {
                controlColor
            }
        }

        playbackModel.loopMode.observe(viewLifecycleOwner) { loopMode ->
            when (loopMode) {
                LoopMode.NONE -> {
                    binding.playbackLoop.imageTintList = controlColor
                    binding.playbackLoop.setImageResource(R.drawable.ic_loop)
                }

                LoopMode.ALL -> {
                    binding.playbackLoop.imageTintList = accentColor
                    binding.playbackLoop.setImageResource(R.drawable.ic_loop)
                }

                LoopMode.TRACK -> {
                    binding.playbackLoop.imageTintList = accentColor
                    binding.playbackLoop.setImageResource(R.drawable.ic_loop_one)
                }

                else -> return@observe
            }
        }

        playbackModel.isSeeking.observe(viewLifecycleOwner) { isSeeking ->
            if (isSeeking) {
                binding.playbackDurationCurrent.setTextColor(accentColor)
            } else {
                binding.playbackDurationCurrent.setTextColor(normalTextColor)
            }
        }

        playbackModel.positionAsProgress.observe(viewLifecycleOwner) { pos ->
            if (!playbackModel.isSeeking.value!!) {
                binding.playbackSeekBar.progress = pos
            }
        }

        playbackModel.nextItemsInQueue.observe(viewLifecycleOwner) { nextQueue ->
            val userQueue = playbackModel.userQueue.value!!

            if (userQueue.isEmpty() && nextQueue.isEmpty()) {
                queueItem.icon = iconQueueInactive
                queueItem.isEnabled = false
            } else {
                queueItem.icon = iconQueueActive
                queueItem.isEnabled = true
            }
        }

        playbackModel.userQueue.observe(viewLifecycleOwner) { userQueue ->
            val nextQueue = playbackModel.nextItemsInQueue.value!!

            if (userQueue.isEmpty() && nextQueue.isEmpty()) {
                queueItem.icon = iconQueueInactive
                queueItem.isEnabled = false
            } else {
                queueItem.icon = iconQueueActive
                queueItem.isEnabled = true
            }
        }

        playbackModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.playbackPlayPause.apply {
                if (isPlaying) {
                    backgroundTintList = accentColor
                    setPlaying(true, playbackModel.canAnimate)
                } else {
                    backgroundTintList = controlColor
                    setPlaying(false, playbackModel.canAnimate)
                }
            }

            playbackModel.enableAnimation()
        }

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            if (item != null) {
                findNavController().navigateUp()
            }
        }

        logD("Fragment Created.")

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        playbackModel.disableAnimation()
    }

    // --- SEEK CALLBACKS ---

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            // Only update the display when seeking, as to have PlaybackService seek
            // [causing possible buffering] on every movement is really odd.
            playbackModel.updatePositionDisplay(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        playbackModel.setSeekingStatus(true)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        playbackModel.setSeekingStatus(false)

        // Confirm the position when seeking stops.
        playbackModel.setPosition(seekBar.progress)
    }
}
