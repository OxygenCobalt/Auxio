package org.oxycblt.auxio.playback

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentCompactPlaybackBinding
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.ui.createToast

/**
 * A [Fragment] that displays the currently played song at a glance, with some basic controls.
 * Extends into [PlaybackFragment] when clicked on.
 *
 * Instantiation is done by FragmentContainerView, **do not instantiate this fragment manually.**
 * @author OxygenCobalt
 */
class CompactPlaybackFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCompactPlaybackBinding.inflate(inflater)

        val iconPauseToPlay = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_pause_to_play
        ) as AnimatedVectorDrawable

        val iconPlayToPause = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_play_to_pause
        ) as AnimatedVectorDrawable

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner

        // Put a placeholder song in the binding & hide the playback fragment initially,
        // just in case.
        binding.song = MusicStore.getInstance().songs[0]
        binding.playbackModel = playbackModel

        binding.root.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionGoToPlayback()
            )
        }

        binding.root.setOnLongClickListener {
            playbackModel.save(requireContext())
            getString(R.string.debug_state_saved).createToast(requireContext())
            true
        }

        // --- VIEWMODEL SETUP ---

        playbackModel.song.observe(viewLifecycleOwner) {
            if (it != null) {
                Log.d(this::class.simpleName, "Updating song display to ${it.name}")

                binding.song = it
                binding.playbackProgress.max = it.seconds.toInt()
            }
        }

        playbackModel.isPlaying.observe(viewLifecycleOwner) {
            if (playbackModel.canAnimate) {
                if (it) {
                    // Animate the icon transition when the playing status switches
                    binding.playbackControls.setImageDrawable(iconPlayToPause)
                    iconPlayToPause.start()
                } else {
                    binding.playbackControls.setImageDrawable(iconPauseToPlay)
                    iconPauseToPlay.start()
                }
            } else {
                if (it) {
                    binding.playbackControls.setImageResource(R.drawable.ic_pause_large)
                } else {
                    binding.playbackControls.setImageResource(R.drawable.ic_play_large)
                }
            }
        }

        playbackModel.positionAsProgress.observe(viewLifecycleOwner) {
            binding.playbackProgress.progress = it
        }

        Log.d(this::class.simpleName, "Fragment Created")

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        playbackModel.resetCanAnimate()
    }
}
