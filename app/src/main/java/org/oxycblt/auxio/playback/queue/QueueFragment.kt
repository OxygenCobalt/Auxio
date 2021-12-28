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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import org.oxycblt.auxio.databinding.FragmentQueueBinding
import org.oxycblt.auxio.playback.PlaybackViewModel

/**
 * A [Fragment] that shows the queue and enables editing as well.
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
        val queueAdapter = QueueAdapter(helper)
        var lastShuffle = playbackModel.isShuffling.value

        callback.addQueueAdapter(queueAdapter)

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner

        binding.queueToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.queueRecycler.apply {
            setHasFixedSize(true)
            adapter = queueAdapter
            helper.attachToRecyclerView(this)
        }

        // --- VIEWMODEL SETUP ----

        playbackModel.nextUp.observe(viewLifecycleOwner) { queue ->
            if (queue.isEmpty()) {
                findNavController().navigateUp()
                return@observe
            }

            queueAdapter.submitList(queue.toMutableList())
        }

        playbackModel.isShuffling.observe(viewLifecycleOwner) { isShuffling ->
            if (isShuffling != lastShuffle) {
                lastShuffle = isShuffling

                binding.queueRecycler.scrollToPosition(0)
            }
        }

        return binding.root
    }
}
