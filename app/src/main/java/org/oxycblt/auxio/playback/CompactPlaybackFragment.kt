package org.oxycblt.auxio.playback

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.BuildConfig
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
            playbackModel.navToItem(playbackModel.song.value!!)
            true
        }

        // Enable the ability to force-save the state in debug builds, in order to check
        // for persistence issues without waiting for PlaybackService to be killed.
        if (BuildConfig.DEBUG) {
            binding.playbackControls.setOnLongClickListener {
                playbackModel.save(requireContext())
                getString(R.string.debug_state_saved).createToast(requireContext())
                true
            }
        }

        // --- VIEWMODEL SETUP ---

        playbackModel.song.observe(viewLifecycleOwner) {
            if (it != null) {
                Log.d(this::class.simpleName, "Updating song display to ${it.name}")

                binding.song = it
                binding.playbackProgress.max = it.seconds.toInt()
            }
        }

        playbackModel.positionAsProgress.observe(viewLifecycleOwner) {
            binding.playbackProgress.progress = it
        }

        Log.d(this::class.simpleName, "Fragment Created")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        playbackModel.disableAnimation()

        // Use the caveman method of getting a view as storing the binding will cause a memory
        // leak.
        val playbackControls = requireView().findViewById<ImageButton>(R.id.playback_controls)

        // Observe the changes to isPlaying for
        val iconPauseToPlay = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_pause_to_play
        ) as AnimatedVectorDrawable

        val iconPlayToPause = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_play_to_pause
        ) as AnimatedVectorDrawable

        playbackModel.isPlaying.observe(viewLifecycleOwner) {
            if (playbackModel.canAnimate) {
                if (it) {
                    // Animate the icon transition when the playing status switches
                    playbackControls?.setImageDrawable(iconPlayToPause)
                    iconPlayToPause.start()
                } else {
                    playbackControls?.setImageDrawable(iconPauseToPlay)
                    iconPauseToPlay.start()
                }
            } else {
                // Use static icons on the first firing of this observer so that the icons
                // don't animate on startup, which looks weird.
                if (it) {
                    playbackControls?.setImageResource(R.drawable.ic_pause_large)
                } else {
                    playbackControls?.setImageResource(R.drawable.ic_play_large)
                }

                playbackModel.enableAnimation()
            }
        }
    }
}
