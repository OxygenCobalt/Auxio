package org.oxycblt.auxio.playback

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentCompactPlaybackBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.ui.isLandscape
import org.oxycblt.auxio.ui.memberBinding

/**
 * A [Fragment] that displays the currently played song at a glance, with some basic controls.
 * Extends into [PlaybackFragment] when clicked on.
 *
 * Instantiation is done by FragmentContainerView, **do not instantiate this fragment manually.**
 * @author OxygenCobalt
 */
class CompactPlaybackFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    private val binding: FragmentCompactPlaybackBinding by memberBinding(
        FragmentCompactPlaybackBinding::inflate
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner

        // Put a placeholder song in the binding & hide the playback fragment initially.
        binding.song = MusicStore.getInstance().songs[0]
        binding.playbackModel = playbackModel

        binding.root.apply {
            setOnClickListener {
                findNavController().navigate(
                    MainFragmentDirections.actionGoToPlayback()
                )
            }

            setOnLongClickListener {
                detailModel.navToItem(playbackModel.song.value!!)
                true
            }
        }

        if (playbackModel.song.value == null) {
            setInvisible(true)
        }

        // --- VIEWMODEL SETUP ---

        playbackModel.song.observe(viewLifecycleOwner) {
            if (it != null) {
                logD("Updating song display to ${it.name}")

                binding.song = it
                binding.playbackProgress.max = it.seconds.toInt()
                setInvisible(false)
            } else {
                setInvisible(true)
            }
        }

        playbackModel.positionAsProgress.observe(viewLifecycleOwner) {
            binding.playbackProgress.progress = it
        }

        logD("Fragment Created")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        playbackModel.disableAnimation()

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
                    binding.playbackControls.setImageDrawable(iconPlayToPause)
                    iconPlayToPause.start()
                } else {
                    binding.playbackControls.setImageDrawable(iconPauseToPlay)
                    iconPauseToPlay.start()
                }
            } else {
                // Use static icons on the first firing of this observer so that the icons
                // don't animate on startup, which looks weird.
                if (it) {
                    binding.playbackControls.setImageResource(R.drawable.ic_pause_large)
                } else {
                    binding.playbackControls.setImageResource(R.drawable.ic_play_large)
                }

                playbackModel.enableAnimation()
            }
        }
    }

    /**
     * Set this fragment to be invisible, if needed. Only runs in landscape mode.
     */
    private fun setInvisible(invisible: Boolean) {
        // Does not run in landscape
        if (!isLandscape(resources)) return

        val visibility = if (invisible) View.INVISIBLE else View.VISIBLE

        binding.playbackLayout.children.forEach {
            it.visibility = visibility
        }

        binding.root.isEnabled = !invisible
    }
}
