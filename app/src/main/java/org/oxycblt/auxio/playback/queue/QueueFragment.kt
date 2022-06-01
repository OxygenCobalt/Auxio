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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.FragmentQueueBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.launch
import org.oxycblt.auxio.util.requireAttached

/**
 * A [Fragment] that shows the queue and enables editing as well.
 * @author OxygenCobalt
 */
class QueueFragment : ViewBindingFragment<FragmentQueueBinding>(), QueueItemListener {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private var queueAdapter = QueueAdapter(this)
    private var touchHelper: ItemTouchHelper? = null
    private var callback: QueueDragCallback? = null

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentQueueBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentQueueBinding, savedInstanceState: Bundle?) {
        binding.queueToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.queueRecycler.apply {
            adapter = queueAdapter
            requireTouchHelper().attachToRecyclerView(this)
        }

        // --- VIEWMODEL SETUP ----

        launch { playbackModel.nextUp.collect(::updateQueue) }
    }

    override fun onDestroyBinding(binding: FragmentQueueBinding) {
        super.onDestroyBinding(binding)
        binding.queueRecycler.adapter = null
        touchHelper = null
        callback = null
    }

    override fun onPickUp(viewHolder: RecyclerView.ViewHolder) {
        requireTouchHelper().startDrag(viewHolder)
    }

    private fun updateQueue(queue: List<Song>) {
        if (queue.isEmpty()) {
            findNavController().navigateUp()
            return
        }

        queueAdapter.data.submitList(queue.toMutableList())
    }

    private fun requireTouchHelper(): ItemTouchHelper {
        requireAttached()
        val instance = touchHelper
        if (instance != null) {
            return instance
        }
        val newCallback = QueueDragCallback(playbackModel, queueAdapter)
        val newInstance = ItemTouchHelper(newCallback)
        callback = newCallback
        touchHelper = newInstance
        return newInstance
    }
}
