package org.oxycblt.auxio.playback.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentQueueBinding
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.applyDivider

/**
 * A [Fragment] that contains both the user queue and the next queue, with the ability to
 * edit them as well.
 *
 * Instantiation is done by the navigation component, **do not instantiate this fragment manually.**
 */
class QueueFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentQueueBinding.inflate(inflater)

        val callback = QueueDragCallback(playbackModel)
        val helper = ItemTouchHelper(callback)
        val queueAdapter = QueueAdapter(helper)
        callback.addQueueAdapter(queueAdapter)

        val queueClearItem = binding.queueToolbar.menu.findItem(R.id.action_clear_user_queue)

        // --- UI SETUP ---

        binding.queueToolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_clear_user_queue) {
                    queueAdapter.clearUserQueue()
                    playbackModel.clearUserQueue()
                    true
                } else false
            }
        }

        binding.queueRecycler.apply {
            setHasFixedSize(true)
            applyDivider()
            adapter = queueAdapter
            helper.attachToRecyclerView(this)
        }

        // --- VIEWMODEL SETUP ---

        playbackModel.userQueue.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                queueClearItem.isEnabled = false

                if (playbackModel.nextItemsInQueue.value!!.isEmpty()) {
                    findNavController().navigateUp()

                    return@observe
                }
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

    private fun createQueueData(): MutableList<BaseModel> {
        val queue = mutableListOf<BaseModel>()

        if (playbackModel.userQueue.value!!.isNotEmpty()) {
            queue.add(
                Header(name = getString(R.string.label_next_user_queue))
            )
            queue.addAll(playbackModel.userQueue.value!!)
        }

        if (playbackModel.nextItemsInQueue.value!!.isNotEmpty()) {
            queue.add(
                Header(
                    name = getString(
                        R.string.format_next_from,
                        if (playbackModel.mode.value == PlaybackMode.ALL_SONGS)
                            getString(R.string.label_all_songs)
                        else
                            playbackModel.parent.value!!.name
                    )
                )
            )
            queue.addAll(playbackModel.nextItemsInQueue.value!!)
        }

        return queue
    }
}
