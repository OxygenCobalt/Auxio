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
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentQueueBinding
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.isEdgeOn
import org.oxycblt.auxio.ui.isIrregularLandscape

/**
 * A [Fragment] that contains both the user queue and the next queue, with the ability to
 * edit them as well.
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
        var lastShuffle = playbackModel.isShuffling.value

        callback.addQueueAdapter(queueAdapter)

        // --- UI SETUP ---

        binding.queueToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.queueRecycler.apply {
            setHasFixedSize(true)
            adapter = queueAdapter
            helper.attachToRecyclerView(this)
        }

        setupEdgeForQueue(binding)

        // --- VIEWMODEL SETUP ----

        playbackModel.userQueue.observe(viewLifecycleOwner) { userQueue ->
            if (userQueue.isEmpty() && playbackModel.nextItemsInQueue.value!!.isEmpty()) {
                findNavController().navigateUp()

                return@observe
            }

            queueAdapter.submitList(createQueueData())
        }

        playbackModel.nextItemsInQueue.observe(viewLifecycleOwner) { nextQueue ->
            if (nextQueue.isEmpty() && playbackModel.userQueue.value!!.isEmpty()) {
                findNavController().navigateUp()
            }

            queueAdapter.submitList(createQueueData())
        }

        playbackModel.isShuffling.observe(viewLifecycleOwner) { isShuffling ->
            if (isShuffling != lastShuffle) {
                lastShuffle = isShuffling

                binding.queueRecycler.scrollToPosition(0)
            }
        }

        return binding.root
    }

    private fun setupEdgeForQueue(binding: FragmentQueueBinding) {
        if (isEdgeOn() && !requireActivity().isIrregularLandscape()) {
            binding.queueToolbar.setOnApplyWindowInsetsListener { v, insets ->
                val top = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    insets.getInsets(WindowInsets.Type.systemBars()).top
                } else {
                    @Suppress("DEPRECATION")
                    insets.systemWindowInsetTop
                }

                (v.parent as View).updatePadding(top = top)

                insets
            }

            binding.queueRecycler.setOnApplyWindowInsetsListener { v, insets ->
                val bottom = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    insets.getInsets(WindowInsets.Type.systemBars()).bottom
                } else {
                    @Suppress("DEPRECATION")
                    insets.systemWindowInsetBottom
                }

                // Apply bottom padding to make sure that the last queue item isnt incorrectly lost,
                // but also make sure that the added padding wont clip the child views entirely.
                (v as RecyclerView).apply {
                    clipToPadding = false
                    updatePadding(bottom = bottom)
                    overScrollMode = RecyclerView.OVER_SCROLL_IF_CONTENT_SCROLLS
                }

                insets
            }
        } else {
            // Don't even bother if we are in phone landscape or if edge-to-edge is off.
            binding.root.fitsSystemWindows = true
        }
    }

    // --- QUEUE DATA ---

    /**
     * Create the queue data that should be displayed
     * @return The list of headers/songs that should be displayed.
     */
    private fun createQueueData(): MutableList<BaseModel> {
        val queue = mutableListOf<BaseModel>()
        val userQueue = playbackModel.userQueue.value!!
        val nextQueue = playbackModel.nextItemsInQueue.value!!

        if (userQueue.isNotEmpty()) {
            queue += Header(
                id = -2,
                name = getString(R.string.label_next_user_queue),
                isAction = true
            )

            queue += userQueue
        }

        if (nextQueue.isNotEmpty()) {
            queue += Header(
                id = -3,
                name = getString(R.string.format_next_from, getParentName()),
                isAction = false
            )

            queue += nextQueue
        }

        return queue
    }

    private fun getParentName(): String {
        return playbackModel.parent.value?.displayName ?: getString(R.string.label_all_songs)
    }
}
