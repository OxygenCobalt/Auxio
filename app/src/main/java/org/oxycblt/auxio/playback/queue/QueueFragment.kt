package org.oxycblt.auxio.playback.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.oxycblt.auxio.databinding.FragmentQueueBinding
import org.oxycblt.auxio.playback.PlaybackViewModel

// TODO: Make this better
class QueueFragment : BottomSheetDialogFragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentQueueBinding.inflate(inflater)

        binding.queueViewpager.adapter = PagerAdapter()

        // TODO: Add option for default queue screen
        if (playbackModel.userQueue.value!!.isEmpty()) {
            binding.queueViewpager.setCurrentItem(1, false)
        } else {
            binding.queueViewpager.setCurrentItem(0, false)
        }

        return binding.root
    }

    private inner class PagerAdapter :
        FragmentStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return QueueListFragment(position)
        }
    }
}
