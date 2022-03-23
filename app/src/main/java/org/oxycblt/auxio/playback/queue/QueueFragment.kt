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
import org.oxycblt.auxio.databinding.FragmentQueueBinding
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.logD

/**
 * A [Fragment] that shows the queue and enables editing as well.
 * @author OxygenCobalt
 */
class QueueFragment : ViewBindingFragment<FragmentQueueBinding>() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private var lastShuffle: Boolean? = null

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentQueueBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentQueueBinding, savedInstanceState: Bundle?) {
        // TODO: Merge ItemTouchHelper with QueueAdapter
        val callback = QueueDragCallback(playbackModel)
        val helper = ItemTouchHelper(callback)
        val queueAdapter = QueueAdapter(helper)
        callback.addQueueAdapter(queueAdapter)

        binding.queueToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.queueRecycler.apply {
            setHasFixedSize(true)
            adapter = queueAdapter
            helper.attachToRecyclerView(this)
        }

        // --- VIEWMODEL SETUP ----

        lastShuffle = playbackModel.isShuffling.value
        playbackModel.isShuffling.observe(viewLifecycleOwner) { isShuffling ->
            // Try to prevent the queue adapter from going spastic during reshuffle events
            // by just scrolling back to the top.
            if (isShuffling != lastShuffle) {
                logD("Reshuffle event, scrolling to top")
                lastShuffle = isShuffling
                binding.queueRecycler.scrollToPosition(0)
            }
        }

        playbackModel.nextUp.observe(viewLifecycleOwner) { queue ->
            if (queue.isEmpty()) {
                findNavController().navigateUp()
                return@observe
            }

            queueAdapter.submitList(queue.toMutableList())
        }
    }
}
