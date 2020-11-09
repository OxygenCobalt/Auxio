package org.oxycblt.auxio.playback.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentQueueBinding
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.applyDivider

class QueueFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentQueueBinding.inflate(inflater)

        val helper = ItemTouchHelper(QueueDragCallback(playbackModel))
        val queueAdapter = QueueAdapter(helper)

        // --- UI SETUP ---

        binding.queueToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.queueRecycler.apply {
            setHasFixedSize(true)
            applyDivider()
            adapter = queueAdapter
            helper.attachToRecyclerView(this)
        }

        playbackModel.userQueue.observe(viewLifecycleOwner) {
            queueAdapter.submitList(createQueueDisplay()) {
                binding.queueRecycler.scrollToPosition(0)
                scrollRecyclerIfNeeded(binding)
            }
        }

        playbackModel.nextItemsInQueue.observe(viewLifecycleOwner) {
            queueAdapter.submitList(createQueueDisplay()) {
                scrollRecyclerIfNeeded(binding)
            }
        }

        return binding.root
    }

    private fun createQueueDisplay(): MutableList<BaseModel> {
        val queue = mutableListOf<BaseModel>()

        if (playbackModel.userQueue.value!!.isNotEmpty()) {
            queue.add(Header(name = getString(R.string.label_next_user_queue)))
            queue.addAll(playbackModel.userQueue.value!!)
        }

        if (playbackModel.nextItemsInQueue.value!!.isNotEmpty()) {
            queue.add(
                Header(
                    name = getString(
                        R.string.format_next_from,
                        if (playbackModel.mode.value == PlaybackMode.ALL_SONGS)
                            getString(R.string.title_all_songs)
                        else
                            playbackModel.parent.value!!.name
                    )
                )
            )
            queue.addAll(playbackModel.nextItemsInQueue.value!!)
        }

        return queue
    }

    private fun scrollRecyclerIfNeeded(binding: FragmentQueueBinding) {
        if ((binding.queueRecycler.layoutManager as LinearLayoutManager)
                .findFirstVisibleItemPosition() < 1
        ) {
            binding.queueRecycler.scrollToPosition(0)
        }
    }
}
