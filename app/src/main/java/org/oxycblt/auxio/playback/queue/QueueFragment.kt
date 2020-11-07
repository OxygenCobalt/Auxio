package org.oxycblt.auxio.playback.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentQueueBinding
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.theme.accent
import org.oxycblt.auxio.theme.applyDivider
import org.oxycblt.auxio.theme.toColor

class QueueFragment : BottomSheetDialogFragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun getTheme(): Int = R.style.Theme_BottomSheetFix

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentQueueBinding.inflate(inflater)

        val helper = ItemTouchHelper(
            QueueDragCallback(playbackModel)
        )
        val queueAdapter = QueueAdapter(helper)

        // --- UI SETUP ---

        binding.queueHeader.setTextColor(accent.first.toColor(requireContext()))
        binding.queueRecycler.apply {
            adapter = queueAdapter
            itemAnimator = DefaultItemAnimator()
            applyDivider()
            setHasFixedSize(true)

            helper.attachToRecyclerView(this)
        }

        // --- VIEWMODEL SETUP ---

        playbackModel.nextItemsInQueue.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                dismiss()

                return@observe
            }

            // If the first item is being moved, then scroll to the top position on completion
            // to prevent ListAdapter from scrolling uncontrollably.
            if (queueAdapter.currentList.isNotEmpty() && it[0].id != queueAdapter.currentList[0].id) {
                queueAdapter.submitList(it.toMutableList()) {
                    // Make sure that the RecyclerView doesn't scroll to the top if the first item
                    // changed, but is not visible.
                    val firstItem = (binding.queueRecycler.layoutManager as LinearLayoutManager)
                        .findFirstVisibleItemPosition()

                    if (firstItem == -1 || firstItem == 0) {
                        binding.queueRecycler.scrollToPosition(0)
                    }
                }
            } else {
                queueAdapter.submitList(it.toMutableList())
            }
        }

        return binding.root
    }
}
