package org.oxycblt.auxio.songs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentSongsBinding
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.PlaybackMode
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
                {
                    playbackModel.playSong(it, PlaybackMode.ALL_SONGS)
                },
                { data, view ->
                    showActionMenuForSong(data, view)
                }
            )
            applyDivider()
            setHasFixedSize(true)
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    private fun showActionMenuForSong(song: Song, view: View) {
        // TODO: Replace this with something nicer
        PopupMenu(requireContext(), view).apply {
            inflate(R.menu.menu_song_actions)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_queue_add) {
                    playbackModel.addToUserQueue(song)

                    return@setOnMenuItemClickListener true
                }

                false
            }
            show()
        }
    }
}
