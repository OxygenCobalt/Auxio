/*
 * Copyright (c) 2021 Auxio Project
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
import androidx.core.view.isInvisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.FragmentQueueBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.logD

/**
 * A [ViewBindingFragment] that displays an editable queue.
 * @author Alexander Capehart (OxygenCobalt)
 */
class QueueFragment : ViewBindingFragment<FragmentQueueBinding>(), QueueAdapter.Listener {
    private val queueModel: QueueViewModel by activityViewModels()
    private val playbackModel: PlaybackViewModel by androidActivityViewModels()
    private val queueAdapter = QueueAdapter(this)
    private val touchHelper: ItemTouchHelper by lifecycleObject {
        ItemTouchHelper(QueueDragCallback(queueModel))
    }

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentQueueBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentQueueBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        // --- UI SETUP ---
        binding.queueRecycler.apply {
            adapter = queueAdapter
            touchHelper.attachToRecyclerView(this)
        }

        // Sometimes the scroll can change without the listener being updated, so we also
        // check for relayout events.
        binding.queueRecycler.apply {
            addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> updateDivider() }
            addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        updateDivider()
                    }
                })
        }

        // --- VIEWMODEL SETUP ----
        collectImmediately(
            queueModel.queue, queueModel.index, playbackModel.isPlaying, ::updateQueue)
    }

    override fun onDestroyBinding(binding: FragmentQueueBinding) {
        super.onDestroyBinding(binding)
        binding.queueRecycler.adapter = null
    }

    override fun onClick(viewHolder: RecyclerView.ViewHolder) {
        // Clicking on a queue item should start playing it.
        queueModel.goto(viewHolder.bindingAdapterPosition)
    }

    override fun onPickUp(viewHolder: RecyclerView.ViewHolder) {
        touchHelper.startDrag(viewHolder)
    }

    private fun updateDivider() {
        val binding = requireBinding()
        binding.queueDivider.isInvisible =
            (binding.queueRecycler.layoutManager as LinearLayoutManager)
                .findFirstCompletelyVisibleItemPosition() < 1
    }

    private fun updateQueue(queue: List<Song>, index: Int, isPlaying: Boolean) {
        val binding = requireBinding()

        // Replace or diff the queue depending on the type of change it is.
        // TODO: Extend this to the whole app.
        if (queueModel.replaceQueue == true) {
            logD("Replacing queue")
            queueAdapter.replaceList(queue)
        } else {
            logD("Diffing queue")
            queueAdapter.submitList(queue)
        }
        queueModel.finishReplace()

        // If requested, scroll to a new item (occurs when the index moves)
        // TODO: Scroll to center/top instead of bottom
        val scrollTo = queueModel.scrollTo
        if (scrollTo != null) {
            // Do not scroll to indices that are in the currently visible range. As that would
            // lead to the queue jumping around every time goto is called.
            val lmm = binding.queueRecycler.layoutManager as LinearLayoutManager
            val start = lmm.findFirstCompletelyVisibleItemPosition()
            val end = lmm.findLastCompletelyVisibleItemPosition()
            if (scrollTo !in start..end) {
                logD("Scrolling to new position")
                binding.queueRecycler.scrollToPosition(scrollTo)
            }
        }
        queueModel.finishScrollTo()

        // Update position in list (and thus past/future items)
        queueAdapter.setPosition(index, isPlaying)
    }
}
