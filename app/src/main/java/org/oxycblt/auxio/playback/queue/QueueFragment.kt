package org.oxycblt.auxio.playback.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
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

        val queueAdapter = QueueAdapter {}

        // --- UI SETUP ---

        binding.queueHeader.setTextColor(accent.first.toColor(requireContext()))
        binding.queueRecycler.apply {
            adapter = queueAdapter
            applyDivider()
            setHasFixedSize(true)
        }

        // --- VIEWMODEL SETUP ---

        playbackModel.formattedQueue.observe(viewLifecycleOwner) {
            queueAdapter.submitList(it)
        }

        return binding.root
    }
}
