package org.oxycblt.auxio.songs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentSongsBinding
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.setupSongActions

class SongsFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSongsBinding.inflate(inflater)

        val musicStore = MusicStore.getInstance()

        // TODO: Add option to search songs [Or just make a dedicated tab]
        // TODO: Fast scrolling?

        // --- UI SETUP ---

        binding.songToolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_shuffle) {
                playbackModel.shuffleAll()
            }
            true
        }

        binding.songRecycler.apply {
            adapter = SongAdapter(
                musicStore.songs,
                doOnClick = { playbackModel.playSong(it, PlaybackMode.ALL_SONGS) },
                doOnLongClick = { data, view ->
                    PopupMenu(requireContext(), view).setupSongActions(
                        data, requireContext(), playbackModel
                    )
                }
            )
            setHasFixedSize(true)
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }
}
