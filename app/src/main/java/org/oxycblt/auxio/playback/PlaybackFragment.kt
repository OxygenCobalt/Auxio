package org.oxycblt.auxio.playback

import android.content.res.ColorStateList
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentPlaybackBinding
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.theme.accent
import org.oxycblt.auxio.theme.disable
import org.oxycblt.auxio.theme.enable
import org.oxycblt.auxio.theme.toColor

// TODO: Add a swipe-to-next-track function using a ViewPager
class PlaybackFragment : Fragment(), SeekBar.OnSeekBarChangeListener {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    // TODO: Implement nav to artists/albums
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPlaybackBinding.inflate(inflater)

        // Create accents & icons to use
        val accentColor = ColorStateList.valueOf(accent.first.toColor(requireContext()))
        val controlColor = ColorStateList.valueOf(R.color.control_color.toColor(requireContext()))
        val normalTextColor = binding.playbackDurationCurrent.currentTextColor

        val iconPauseToPlay = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_pause_to_play
        ) as AnimatedVectorDrawable

        val iconPlayToPause = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_play_to_pause
        ) as AnimatedVectorDrawable

        // Can't set the tint of a MenuItem below Android 8, so use icons instead.
        val iconQueueActive = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_queue
        )

        val iconQueueInactive = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_queue_inactive
        )

        val queueMenuItem: MenuItem

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner
        binding.playbackModel = playbackModel
        binding.song = playbackModel.song.value!!

        binding.playbackToolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_queue) {
                    findNavController().navigate(PlaybackFragmentDirections.actionShowQueue())
                }

                true
            }

            queueMenuItem = menu.findItem(R.id.action_queue)
        }

        // Make marquee scroll work
        binding.playbackSong.isSelected = true

        binding.playbackSeekBar.setOnSeekBarChangeListener(this)

        // --- VIEWMODEL SETUP --

        playbackModel.song.observe(viewLifecycleOwner) {
            if (it != null) {
                Log.d(this::class.simpleName, "Updating song display to ${it.name}.")

                binding.song = it
                binding.playbackSeekBar.max = it.seconds.toInt()
            } else {
                Log.d(this::class.simpleName, "No song played anymore, leaving.")

                findNavController().navigateUp()
            }
        }

        playbackModel.index.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.playbackSkipPrev.enable(requireContext())
            } else {
                binding.playbackSkipPrev.disable(requireContext())
            }

            if (it < playbackModel.queue.value!!.lastIndex) {
                binding.playbackSkipNext.enable(requireContext())
            } else {
                binding.playbackSkipNext.disable(requireContext())
            }
        }

        playbackModel.isPlaying.observe(viewLifecycleOwner) {
            if (it) {
                // Animate the playing status and switch the button to the accent color
                // if its playing, and back to a inactive gray if not.
                binding.playbackPlayPause.setImageDrawable(iconPauseToPlay)
                iconPauseToPlay.start()

                binding.playbackPlayPause.backgroundTintList = accentColor
            } else {
                binding.playbackPlayPause.setImageDrawable(iconPlayToPause)
                iconPlayToPause.start()

                binding.playbackPlayPause.backgroundTintList = controlColor
            }
        }

        playbackModel.isShuffling.observe(viewLifecycleOwner) {
            // Highlight the shuffle button if Playback is shuffled, and revert it if not.
            if (it) {
                binding.playbackShuffle.imageTintList = accentColor
            } else {
                binding.playbackShuffle.imageTintList = controlColor
            }
        }

        playbackModel.loopMode.observe(viewLifecycleOwner) {
            when (it) {
                LoopMode.NONE -> {
                    binding.playbackLoop.imageTintList = controlColor
                    binding.playbackLoop.setImageResource(R.drawable.ic_loop_large)
                }
                LoopMode.ONCE -> {
                    binding.playbackLoop.imageTintList = accentColor
                    binding.playbackLoop.setImageResource(R.drawable.ic_loop_one_large)
                }
                LoopMode.INFINITE -> {
                    binding.playbackLoop.imageTintList = accentColor
                    binding.playbackLoop.setImageResource(R.drawable.ic_loop_large)
                }

                else -> return@observe
            }
        }

        playbackModel.isSeeking.observe(viewLifecycleOwner) {
            // Highlight the current duration if the user is seeking, and revert it if not.
            if (it) {
                binding.playbackDurationCurrent.setTextColor(accentColor)
            } else {
                binding.playbackDurationCurrent.setTextColor(normalTextColor)
            }
        }

        // Updates for the current duration TextView/Seekbar
        playbackModel.formattedPosition.observe(viewLifecycleOwner) {
            binding.playbackDurationCurrent.text = it
        }

        playbackModel.positionAsProgress.observe(viewLifecycleOwner) {
            if (!playbackModel.isSeeking.value!!) {
                binding.playbackSeekBar.progress = it
            }
        }

        playbackModel.nextItemsInQueue.observe(viewLifecycleOwner) {
            // Disable the option to open the queue if there's nothing in it.
            if (it.isEmpty() && playbackModel.userQueue.value!!.isEmpty()) {
                queueMenuItem.isEnabled = false
                queueMenuItem.icon = iconQueueInactive
            } else {
                queueMenuItem.isEnabled = true
                queueMenuItem.icon = iconQueueActive
            }

            // If someone edits the queue to make it have no songs left, then disable the
            // skip next button.
            if (playbackModel.index.value!! == it.size) {
                binding.playbackSkipNext.disable(requireContext())
            } else {
                binding.playbackSkipNext.enable(requireContext())
            }
        }

        playbackModel.userQueue.observe(viewLifecycleOwner) {
            if (it.isEmpty() && playbackModel.queue.value!!.isEmpty()) {
                queueMenuItem.isEnabled = false
                queueMenuItem.icon = iconQueueInactive
            } else {
                queueMenuItem.isEnabled = true
                queueMenuItem.icon = iconQueueActive
            }
        }

        Log.d(this::class.simpleName, "Fragment Created.")

        return binding.root
    }

    // Seeking callbacks
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            playbackModel.updatePositionDisplay(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        playbackModel.setSeekingStatus(true)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        playbackModel.setSeekingStatus(false)

        playbackModel.updatePosition(seekBar.progress)
    }
}
