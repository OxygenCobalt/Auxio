package org.oxycblt.auxio.songs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.oxycblt.auxio.databinding.FragmentSongsBinding
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.theme.applyDivider

class SongsFragment : Fragment() {
    private val musicModel: MusicViewModel by activityViewModels {
        MusicViewModel.Factory(requireActivity().application)
    }

    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSongsBinding.inflate(inflater)

        // TODO: Add option to search songs if LibraryFragment isn't enabled
        // TODO: Maybe add fast scrolling or sorting

        // --- UI SETUP ---

        binding.songRecycler.apply {
            adapter = SongAdapter(
                musicModel.songs.value!!,
                ClickListener { song ->
                    playbackModel.updateSong(song)
                }
            )
            applyDivider()
            setHasFixedSize(true)
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }
}
