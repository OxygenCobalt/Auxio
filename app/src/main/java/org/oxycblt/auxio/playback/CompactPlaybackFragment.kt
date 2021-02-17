package org.oxycblt.auxio.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.databinding.FragmentCompactPlaybackBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.MusicStore
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
    private val binding by memberBinding(FragmentCompactPlaybackBinding::inflate)

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

        // --- VIEWMODEL SETUP ---

        playbackModel.song.observe(viewLifecycleOwner) {
            if (it != null) {
                logD("Updating song display to ${it.name}")

                binding.song = it
                binding.playbackProgress.max = it.seconds.toInt()
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

        playbackModel.isPlaying.observe(viewLifecycleOwner) {
            if (it) {
                binding.playbackPlayPause.showPause(playbackModel.canAnimate)
            } else {
                binding.playbackPlayPause.showPlay(playbackModel.canAnimate)
            }

            playbackModel.enableAnimation()
        }
    }
}
