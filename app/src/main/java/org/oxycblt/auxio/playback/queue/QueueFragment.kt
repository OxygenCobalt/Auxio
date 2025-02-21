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
import androidx.core.view.isInvisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.min
import org.oxycblt.auxio.databinding.FragmentQueueBinding
import org.oxycblt.auxio.list.EditClickListListener
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.musikr.Song
import timber.log.Timber as L

/**
 * A [ViewBindingFragment] that displays an editable queue.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class QueueFragment : ViewBindingFragment<FragmentQueueBinding>(), EditClickListListener<Song> {
    private val queueModel: QueueViewModel by viewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val queueAdapter = QueueAdapter(this)
    private var touchHelper: ItemTouchHelper? = null

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentQueueBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentQueueBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        // --- UI SETUP ---
        binding.queueRecycler.apply {
            adapter = queueAdapter
            touchHelper =
                ItemTouchHelper(QueueDragCallback(queueModel)).also {
                    it.attachToRecyclerView(this)
                }
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
        touchHelper = null
        binding.queueRecycler.adapter = null
        // Avoid possible race conditions that could cause a bad instruction to be consumed
        // during list initialization and crash the app. Could happen if the user is fast enough.
        queueModel.queueInstructions.consume()
    }

    override fun onClick(item: Song, viewHolder: RecyclerView.ViewHolder) {
        queueModel.goto(viewHolder.bindingAdapterPosition)
    }

    override fun onPickUp(viewHolder: RecyclerView.ViewHolder) {
        requireNotNull(touchHelper) { "ItemTouchHelper was not available" }.startDrag(viewHolder)
    }

    private fun updateDivider() {
        val binding = requireBinding()
        binding.queueDivider.isInvisible =
            (binding.queueRecycler.layoutManager as LinearLayoutManager)
                .findFirstCompletelyVisibleItemPosition() < 1
    }

    private fun updateQueue(queue: List<Song>, index: Int, isPlaying: Boolean) {
        val binding = requireBinding()

        queueAdapter.update(queue, queueModel.queueInstructions.consume())
        queueAdapter.setPosition(index, isPlaying)

        // If requested, scroll to a new item (occurs when the index moves)
        val scrollTo = queueModel.scrollTo.consume()
        if (scrollTo != null) {
            val lmm = binding.queueRecycler.layoutManager as LinearLayoutManager
            val start = lmm.findFirstCompletelyVisibleItemPosition()
            val end = lmm.findLastCompletelyVisibleItemPosition()
            val notInitialized =
                start == RecyclerView.NO_POSITION || end == RecyclerView.NO_POSITION
            // When we scroll, we want to scroll to the almost-top so the user can see
            // future songs instead of past songs. The way we have to do this however is
            // dependent on where we have to scroll to get to the currently playing song.
            if (notInitialized || scrollTo < start) {
                // We need to scroll upwards, or initialize the scroll, no need to offset
                L.d("Not scrolling downwards, no offset needed")
                binding.queueRecycler.scrollToPosition(scrollTo)
            } else if (scrollTo > end) {
                // We need to scroll downwards, we need to offset by a screen of songs.
                // This does have some error due to how many completely visible items on-screen
                // can vary. This is considered okay.
                val offset = scrollTo + (end - start)
                L.d("Scrolling downwards, offsetting by $offset")
                binding.queueRecycler.scrollToPosition(min(queue.lastIndex, offset))
            }
        }
    }
}
