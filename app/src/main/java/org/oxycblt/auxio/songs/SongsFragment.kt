package org.oxycblt.auxio.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentSongsBinding
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.fixAnimInfoLeak
import org.oxycblt.auxio.ui.getSpans
import org.oxycblt.auxio.ui.newMenu

/**
 * A [Fragment] that shows a list of all songs on the device.
 * Contains options to search/shuffle them.
 * @author OxygenCobalt
 */
class SongsFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val musicStore = MusicStore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSongsBinding.inflate(inflater)
        val songAdapter = SongsAdapter(musicStore.songs, playbackModel::playSong) { view, data ->
            newMenu(view, data)
        }

        // --- UI SETUP ---

        binding.songToolbar.apply {
            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_shuffle) {
                    playbackModel.shuffleAll()
                    true
                } else false
            }
        }

        binding.songRecycler.apply {
            adapter = songAdapter
            setHasFixedSize(true)

            val spans = getSpans()
            if (spans != 1) {
                layoutManager = GridLayoutManager(requireContext(), spans)
            }
        }

        binding.songFastScroll.setup(binding.songRecycler) { pos ->
            val char = musicStore.songs[pos].name.first

            if (char.isDigit()) '#' else char
        }

        logD("Fragment created.")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        fixAnimInfoLeak()
    }

    /**
     * Dumb shortcut for getting the first letter in a string, while regarding certain
     * semantics when it comes to articles.
     */
    private val String.first: Char get() {
        // If the name actually starts with "The" or "A", get the character *after* that word.
        // Yes, this is stupidly english centric but it wont run with other languages.
        if (length > 5 && startsWith("the ", true)) {
            return get(4).uppercaseChar()
        }

        if (length > 3 && startsWith("a ", true)) {
            return get(2).uppercaseChar()
        }

        return get(0).uppercaseChar()
    }
}
