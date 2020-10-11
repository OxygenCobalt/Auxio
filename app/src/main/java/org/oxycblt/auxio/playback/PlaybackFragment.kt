package org.oxycblt.auxio.playback

import android.content.res.ColorStateList
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentPlaybackBinding
import org.oxycblt.auxio.theme.accent
import org.oxycblt.auxio.theme.toColor

class PlaybackFragment : BottomSheetDialogFragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    // TODO: Implement nav to artists/albums
    // TODO: Add a full playback fragment
    // TODO: Possibly implement a trackbar with a spectrum shown as well.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPlaybackBinding.inflate(inflater)

        val accentColor = ColorStateList.valueOf(accent.first.toColor(requireContext()))
        val inactiveColor = ColorStateList.valueOf(R.color.control_color.toColor(requireContext()))

        val iconPauseToPlay = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_pause_to_play
        ) as AnimatedVectorDrawable

        val iconPlayToPause = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_play_to_pause
        ) as AnimatedVectorDrawable

        // --- UI SETUP ---

        binding.playbackModel = playbackModel

        // Make marquee scroll work
        binding.playbackSong.isSelected = true
        binding.playbackAlbum.isSelected = true
        binding.playbackArtist.isSelected = true

        // Override the accents manually because the BottomSheetFragment is too dumb to do it themselves.
        binding.playbackSeekBar.thumbTintList = accentColor
        binding.playbackSeekBar.progressTintList = accentColor
        binding.playbackSeekBar.progressBackgroundTintList = accentColor

        // --- VIEWMODEL SETUP --

        playbackModel.currentSong.observe(viewLifecycleOwner) {
            binding.song = it
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

                binding.playbackPlayPause.backgroundTintList = inactiveColor
            }
        }

        return binding.root
    }
}
