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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.FragmentQueueBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.fragment.ViewBindingFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.logD

/**
 * A [Fragment] that shows the queue and enables editing as well.
 *
 * @author OxygenCobalt
 */
class QueueFragment : ViewBindingFragment<FragmentQueueBinding>(), QueueItemListener {
    private val queueModel: QueueViewModel by activityViewModels()
    private val queueAdapter = QueueAdapter(this)
    private val touchHelper: ItemTouchHelper by lifecycleObject {
        ItemTouchHelper(QueueDragCallback(queueModel))
    }

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentQueueBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentQueueBinding, savedInstanceState: Bundle?) {
        binding.queueRecycler.apply {
            adapter = queueAdapter
            touchHelper.attachToRecyclerView(this)
            addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        binding.queueDivider.isInvisible =
                            (layoutManager as LinearLayoutManager)
                                .findFirstCompletelyVisibleItemPosition() < 1
                    }
                })
        }

        // --- VIEWMODEL SETUP ----

        collectImmediately(queueModel.queue, queueModel.index, ::updateQueue)
    }

    override fun onDestroyBinding(binding: FragmentQueueBinding) {
        super.onDestroyBinding(binding)
        binding.queueRecycler.adapter = null
    }

    override fun onClick(viewHolder: RecyclerView.ViewHolder) {
        queueModel.goto(viewHolder.bindingAdapterPosition)
    }

    override fun onPickUp(viewHolder: RecyclerView.ViewHolder) {
        touchHelper.startDrag(viewHolder)
    }

    private fun updateQueue(queue: List<Song>, index: Int) {
        val replaceQueue = queueModel.replaceQueue
        if (replaceQueue == true) {
            logD("Replacing queue")
            queueAdapter.data.replaceList(queue)
        } else {
            logD("Diffing queue")
            queueAdapter.data.submitList(queue)
        }

        queueModel.finishReplace()

        val scrollTo = queueModel.scrollTo
        if (scrollTo != null) {
            val binding = requireBinding()
            val lmm = binding.queueRecycler.layoutManager as LinearLayoutManager
            val start = lmm.findFirstCompletelyVisibleItemPosition()
            val end = lmm.findLastCompletelyVisibleItemPosition()

            if (start != RecyclerView.NO_POSITION &&
                end != RecyclerView.NO_POSITION &&
                scrollTo !in start..end) {
                logD("Scrolling to new position")
                binding.queueRecycler.scrollToPosition(scrollTo)
            }
        }

        queueModel.finishScrollTo()

        queueAdapter.updateIndex(index)
    }
}
