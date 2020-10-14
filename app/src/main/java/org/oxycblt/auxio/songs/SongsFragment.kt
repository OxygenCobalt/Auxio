package org.oxycblt.auxio.songs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.oxycblt.auxio.databinding.FragmentSongsBinding
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.playback.PlaybackMode
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.theme.applyDivider

class SongsFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSongsBinding.inflate(inflater)

        val musicStore = MusicStore.getInstance()

        // TODO: Add option to search songs if LibraryFragment isn't enabled
        // TODO: Maybe add fast scrolling or sorting

        // --- UI SETUP ---

        binding.songRecycler.apply {
            adapter = SongAdapter(musicStore.songs) {
                playbackModel.update(it, PlaybackMode.ALL_SONGS)
            }
            applyDivider()
            setHasFixedSize(true)
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }
}
