package org.oxycblt.auxio.playback

import android.content.res.ColorStateList
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentPlaybackBinding
import org.oxycblt.auxio.theme.accent
import org.oxycblt.auxio.theme.disable
import org.oxycblt.auxio.theme.enable
import org.oxycblt.auxio.theme.toColor

class PlaybackFragment : Fragment(), SeekBar.OnSeekBarChangeListener {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    // TODO: Implement media controls
    // TODO: Make exit icon bigger
    // TODO: Implement nav to artists/albums
    // TODO: Possibly implement a trackbar with a spectrum shown as well.
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
        val disabledColor = ColorStateList.valueOf(
            R.color.inactive_color.toColor(requireContext())
        )

        val iconPauseToPlay = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_pause_to_play
        ) as AnimatedVectorDrawable

        val iconPlayToPause = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_play_to_pause
        ) as AnimatedVectorDrawable

        // --- UI SETUP ---

        binding.playbackModel = playbackModel

        binding.playbackToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Make marquee scroll work
        binding.playbackSong.isSelected = true

        binding.playbackSeekBar.setOnSeekBarChangeListener(this)

        // --- VIEWMODEL SETUP --

        playbackModel.currentSong.observe(viewLifecycleOwner) {
            Log.d(this::class.simpleName, "Updating song display to ${it.name}.")

            binding.song = it
            binding.playbackSeekBar.max = it.seconds.toInt()
        }

        playbackModel.currentIndex.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.playbackSkipPrev.enable(requireContext())
            } else {
                binding.playbackSkipPrev.disable(requireContext())
            }

            Log.d(this::class.simpleName, it.toString())

            if (it < playbackModel.queue.value!!.lastIndex) {
                binding.playbackSkipNext.enable(requireContext())
            } else {
                Log.d(this::class.simpleName, "Fucking stupid retard.")
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

        playbackModel.isSeeking.observe(viewLifecycleOwner) {
            // Highlight the current duration if the user is seeking, and revert it if not.
            if (it) {
                binding.playbackDurationCurrent.setTextColor(accentColor)
            } else {
                binding.playbackDurationCurrent.setTextColor(normalTextColor)
            }
        }

        // Updates for the current duration TextView/Seekbar
        playbackModel.formattedCurrentDuration.observe(viewLifecycleOwner) {
            binding.playbackDurationCurrent.text = it
        }

        playbackModel.formattedSeekBarProgress.observe(viewLifecycleOwner) {
            binding.playbackSeekBar.progress = it
        }

        Log.d(this::class.simpleName, "Fragment Created.")

        return binding.root
    }

    // Seeking callbacks
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            playbackModel.updateCurrentDurationWithProgress(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        playbackModel.setSeekingStatus(true)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        playbackModel.setSeekingStatus(false)
    }
}
