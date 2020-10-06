package org.oxycblt.auxio.playback

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.oxycblt.auxio.databinding.FragmentCompactPlaybackBinding
import org.oxycblt.auxio.music.MusicViewModel

class CompactPlaybackFragment : Fragment() {
    private val musicModel: MusicViewModel by activityViewModels {
        MusicViewModel.Factory(requireActivity().application)
    }

    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCompactPlaybackBinding.inflate(inflater)

        // --- UI SETUP ---

        binding.lifecycleOwner = this

        // Put a placeholder song in the binding & hide the playback fragment initially,
        // as for some reason the attach event doesn't register anymore w/LiveData
        binding.song = musicModel.songs.value!![0]
        binding.root.visibility = View.GONE

        // --- VIEWMODEL SETUP ---

        // TODO: Add some kind of animation to when this view becomes visible/invisible.
        playbackModel.currentSong.observe(viewLifecycleOwner) {
            if (it == null) {
                Log.d(this::class.simpleName, "Hiding playback bar due to no song being played.")

                binding.root.visibility = View.GONE
            } else {
                Log.d(this::class.simpleName, "Updating song display to ${it.name}")

                binding.song = it

                binding.root.visibility = View.VISIBLE
            }
        }

        Log.d(this::class.simpleName, "Fragment Created")

        return binding.root
    }
}
