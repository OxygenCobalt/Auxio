package org.oxycblt.auxio.playback.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentQueueListBinding
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.theme.applyDivider

class QueueListFragment(private val type: Int) : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentQueueListBinding.inflate(inflater)

        // --- UI SETUP ---

        binding.queueRecycler.apply {
            itemAnimator = DefaultItemAnimator()
            applyDivider()
            setHasFixedSize(true)
        }

        // Continue setup with different values depending on the type
        when (type) {
            TYPE_NEXT_QUEUE -> setupForNextQueue(binding)
            TYPE_USER_QUEUE -> setupForUserQueue(binding)
        }

        return binding.root
    }

    private fun setupForNextQueue(binding: FragmentQueueListBinding) {
        val helper = ItemTouchHelper(QueueDragCallback(playbackModel, false))
        val queueNextAdapter = QueueAdapter(helper)

        binding.queueRecycler.apply {
            adapter = queueNextAdapter
            helper.attachToRecyclerView(this)
        }

        playbackModel.mode.observe(viewLifecycleOwner) {
            if (it == PlaybackMode.ALL_SONGS) {
                binding.queueHeader.setText(R.string.label_next_songs)
            } else {
                binding.queueHeader.text = getString(
                    R.string.format_next_from, playbackModel.parent.value!!.name
                )
            }
        }

        playbackModel.nextItemsInQueue.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                if (playbackModel.userQueue.value!!.isEmpty()) {
                    findNavController().navigateUp()
                } else {
                    binding.queueNothingIndicator.visibility = View.VISIBLE
                    binding.queueRecycler.visibility = View.GONE
                }

                return@observe
            }

            binding.queueNothingIndicator.visibility = View.GONE
            binding.queueRecycler.visibility = View.VISIBLE

            // If the first item is being moved, then scroll to the top position on completion
            // to prevent ListAdapter from scrolling uncontrollably.
            if (queueNextAdapter.currentList.isNotEmpty() &&
                it[0].id != queueNextAdapter.currentList[0].id
            ) {
                queueNextAdapter.submitList(it.toMutableList()) {
                    scrollRecyclerIfNeeded(binding)
                }
            } else {
                queueNextAdapter.submitList(it.toMutableList())
            }
        }
    }

    private fun setupForUserQueue(binding: FragmentQueueListBinding) {
        val helper = ItemTouchHelper(QueueDragCallback(playbackModel, true))
        val userQueueAdapter = QueueAdapter(helper)

        binding.queueHeader.setText(R.string.label_next_user_queue)

        binding.queueRecycler.apply {
            adapter = userQueueAdapter
            helper.attachToRecyclerView(this)
        }

        playbackModel.userQueue.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                if (playbackModel.queue.value!!.isEmpty()) {
                    findNavController().navigateUp()
                } else {
                    binding.queueNothingIndicator.visibility = View.VISIBLE
                    binding.queueRecycler.visibility = View.GONE
                }

                return@observe
            }

            binding.queueNothingIndicator.visibility = View.GONE
            binding.queueRecycler.visibility = View.VISIBLE

            // If the first item is being moved, then scroll to the top position on completion
            // to prevent ListAdapter from scrolling uncontrollably.
            if (userQueueAdapter.currentList.isNotEmpty() &&
                it[0].id != userQueueAdapter.currentList[0].id
            ) {
                userQueueAdapter.submitList(it.toMutableList()) {
                    scrollRecyclerIfNeeded(binding)
                }
            } else {
                userQueueAdapter.submitList(it.toMutableList())
            }
        }
    }

    private fun scrollRecyclerIfNeeded(binding: FragmentQueueListBinding) {
        if ((binding.queueRecycler.layoutManager as LinearLayoutManager)
            .findFirstVisibleItemPosition() < 1
        ) {
            binding.queueRecycler.scrollToPosition(0)
        }
    }

    companion object {
        const val TYPE_USER_QUEUE = 0
        const val TYPE_NEXT_QUEUE = 1
    }
}
