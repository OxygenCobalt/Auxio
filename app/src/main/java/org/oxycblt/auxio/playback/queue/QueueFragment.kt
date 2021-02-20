package org.oxycblt.auxio.playback.queue

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentQueueBinding
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.handleFileIntent
import org.oxycblt.auxio.ui.isEdgeOn
import org.oxycblt.auxio.ui.isIrregularLandscape

/**
 * A [Fragment] that contains both the user queue and the next queue, with the ability to
 * edit them as well.
 *
 * Instantiation is done by the navigation component, **do not instantiate this fragment manually.**
 * @author OxygenCobalt
 */
class QueueFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentQueueBinding.inflate(inflater)

        val callback = QueueDragCallback(playbackModel)
        val helper = ItemTouchHelper(callback)
        val queueAdapter = QueueAdapter(helper, playbackModel)

        callback.addQueueAdapter(queueAdapter)

        // --- UI SETUP ---

        binding.queueToolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            if (!requireActivity().isIrregularLandscape() && isEdgeOn()) {
                setOnApplyWindowInsetsListener @Suppress("DEPRECATION") { _, insets ->
                    val top = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        insets.getInsets(WindowInsets.Type.systemBars()).top
                    } else {
                        insets.systemWindowInsetTop
                    }

                    (parent as View).updatePadding(top = top)

                    insets
                }
            } else {
                // Dont even bother w/edge-to-edge if the navigation bar is on the side
                binding.root.fitsSystemWindows = true
            }
        }

        binding.queueRecycler.apply {
            setHasFixedSize(true)
            adapter = queueAdapter
            helper.attachToRecyclerView(this)
        }

        // --- VIEWMODEL SETUP ----

        playbackModel.userQueue.observe(viewLifecycleOwner) {
            if (it.isEmpty() && playbackModel.nextItemsInQueue.value!!.isEmpty()) {
                findNavController().navigateUp()

                return@observe
            }

            queueAdapter.submitList(createQueueData())
        }

        playbackModel.nextItemsInQueue.observe(viewLifecycleOwner) {
            if (it.isEmpty() && playbackModel.userQueue.value!!.isEmpty()) {
                findNavController().navigateUp()
            }

            queueAdapter.submitList(createQueueData())
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        handleFileIntent(playbackModel)
    }

    /**
     * Create the queue data that should be displayed
     * @return The list of headers/songs that should be displayed.
     */
    private fun createQueueData(): MutableList<BaseModel> {
        val queue = mutableListOf<BaseModel>()

        if (playbackModel.userQueue.value!!.isNotEmpty()) {
            queue.add(Header(id = -2, name = getString(R.string.label_next_user_queue), isAction = true))
            queue.addAll(playbackModel.userQueue.value!!)
        }

        if (playbackModel.nextItemsInQueue.value!!.isNotEmpty()) {
            queue.add(
                Header(
                    id = -3,
                    name = getString(R.string.format_next_from, getParentName()),
                    isAction = false
                )
            )
            queue.addAll(playbackModel.nextItemsInQueue.value!!)
        }

        return queue
    }

    private fun getParentName(): String {
        return if (playbackModel.mode.value == PlaybackMode.ALL_SONGS) {
            getString(R.string.label_all_songs)
        } else {
            if (playbackModel.parent.value is Genre) {
                // Use display name for Genres so that numbers dont show up
                (playbackModel.parent.value as Genre).displayName
            } else {
                playbackModel.parent.value!!.name
            }
        }
    }
}
