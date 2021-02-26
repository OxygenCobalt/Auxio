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
import org.oxycblt.auxio.databinding.FragmentPlaybackBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.memberBinding
import org.oxycblt.auxio.ui.toDrawable
import org.oxycblt.auxio.ui.toStateList

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
        // TODO: Add a swipe-to-next-track function using a ViewPager
        //  Would require writing my own variant though to avoid index updates

        val normalTextColor = binding.playbackDurationCurrent.currentTextColor
        val accentColor = Accent.get().getStateList(requireContext())
        val controlColor = R.color.control_color.toStateList(requireContext())

        // Can't set the tint of a MenuItem below Android 8, so use icons instead.
        val iconQueueActive = R.drawable.ic_queue.toDrawable(requireContext())
        val iconQueueInactive = R.drawable.ic_queue_inactive.toDrawable(requireContext())

        val queueItem: MenuItem

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner
        binding.playbackModel = playbackModel
        binding.detailModel = detailModel
        binding.song = playbackModel.song.value!!

        binding.playbackToolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_queue) {
                    findNavController().navigate(PlaybackFragmentDirections.actionShowQueue())

                    true
                } else false
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

        playbackModel.isShuffling.observe(viewLifecycleOwner) {
            binding.playbackShuffle.imageTintList = if (it) accentColor else controlColor
        }

        playbackModel.loopMode.observe(viewLifecycleOwner) {
            when (it) {
                LoopMode.NONE -> {
                    binding.playbackLoop.imageTintList = controlColor
                    binding.playbackLoop.setImageResource(R.drawable.ic_loop)
                }

                LoopMode.ONCE -> {
                    binding.playbackLoop.imageTintList = accentColor
                    binding.playbackLoop.setImageResource(R.drawable.ic_loop_one)
                }

                LoopMode.INFINITE -> {
                    binding.playbackLoop.imageTintList = accentColor
                    binding.playbackLoop.setImageResource(R.drawable.ic_loop)
                }

                else -> return@observe
            }
        }

        playbackModel.isSeeking.observe(viewLifecycleOwner) {
            if (it) {
                binding.playbackDurationCurrent.setTextColor(accentColor)
            } else {
                binding.playbackDurationCurrent.setTextColor(normalTextColor)
            }
        }

        playbackModel.positionAsProgress.observe(viewLifecycleOwner) {
            if (!playbackModel.isSeeking.value!!) {
                binding.playbackSeekBar.progress = it
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

        playbackModel.isPlaying.observe(viewLifecycleOwner) {
            binding.playbackPlayPause.apply {
                if (it) {
                    backgroundTintList = accentColor
                    setPlaying(true, playbackModel.canAnimate)
                } else {
                    backgroundTintList = controlColor
                    setPlaying(false, playbackModel.canAnimate)
                }
            }

            playbackModel.enableAnimation()
        }

        detailModel.navToItem.observe(viewLifecycleOwner) {
            if (it != null) {
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
