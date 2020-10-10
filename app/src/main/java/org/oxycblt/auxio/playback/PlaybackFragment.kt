package org.oxycblt.auxio.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.oxycblt.auxio.databinding.FragmentPlaybackBinding

class PlaybackFragment : BottomSheetDialogFragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    // TODO: Implement nav to artists/albums
    // TODO: Possibly implement a trackbar with a spectrum shown as well.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPlaybackBinding.inflate(inflater)

        // --- UI SETUP ---

        // Make marquee scroll work
        binding.songName.isSelected = true
        binding.songAlbum.isSelected = true
        binding.songArtist.isSelected = true

        // --- VIEWMODEL SETUP --
        playbackModel.currentSong.observe(viewLifecycleOwner) {
            binding.song = it
        }

        return binding.root
    }
}
