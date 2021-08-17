/*
 * Copyright (c) 2021 Auxio Project
 * SongsFragment.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
import org.oxycblt.auxio.recycler.sliceArticle
import org.oxycblt.auxio.spans
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
        val songAdapter = SongsAdapter(musicStore.songs, playbackModel::playSong, ::newMenu)

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

            if (spans != 1) {
                layoutManager = GridLayoutManager(requireContext(), spans)
            }
        }

        binding.songFastScroll.setup(binding.songRecycler) { pos ->
            // Get the first character [respecting articles]
            val char = musicStore.songs[pos].name.sliceArticle().first().uppercaseChar()

            if (char.isDigit()) '#' else char
        }

        logD("Fragment created.")

        return binding.root
    }
}
