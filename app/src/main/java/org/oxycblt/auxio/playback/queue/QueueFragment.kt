/*
 * Copyright (c) 2021 Auxio Project
 * QueueFragment.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.oxycblt.auxio.playback.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.coroutines.NonDisposableHandle.parent
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentQueueBinding
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.applyEdge

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

        val callback = QueueDragCallback(
            playbackModel,
            binding.queueCoordinator,
            binding.queueAppbar
        )

        val helper = ItemTouchHelper(callback)
        val queueAdapter = QueueAdapter(helper, playbackModel)
        var lastShuffle = playbackModel.isShuffling.value

        callback.addQueueAdapter(queueAdapter)

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner

        binding.applyEdge { bars ->
            binding.root.updatePadding(left = bars.left, right = bars.right)
            binding.queueAppbar.updatePadding(top = bars.top)
            binding.queueRecycler.updatePadding(bottom = bars.bottom)
        }

        binding.queueToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.queueRecycler.apply {
            setHasFixedSize(true)
            adapter = queueAdapter
            helper.attachToRecyclerView(this)
        }

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
                // binding.queueAppbar.isLifted = false // Make sure lifted state changes.
            }
        }

        return binding.root
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
                name = getString(R.string.lbl_next_user_queue),
                isAction = true
            )

            queue += userQueue
        }

        if (nextQueue.isNotEmpty()) {
            queue += Header(
                id = -3,
                name = getString(R.string.fmt_next_from, getParentName()),
                isAction = false
            )

            queue += nextQueue
        }

        return queue
    }

    private fun getParentName(): String {
        return playbackModel.parent.value?.displayName ?: getString(R.string.lbl_all_songs)
    }
}
