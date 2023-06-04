/*
 * Copyright (c) 2023 Auxio Project
 * PlaylistDetailHeaderAdapter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.detail.header

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemDetailHeaderBinding
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.logD

/**
 * A [DetailHeaderAdapter] that shows [Playlist] information.
 *
 * @param listener [DetailHeaderAdapter.Listener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class PlaylistDetailHeaderAdapter(private val listener: Listener) :
    DetailHeaderAdapter<Playlist, PlaylistDetailHeaderViewHolder>() {
    private var editedPlaylist: List<Song>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PlaylistDetailHeaderViewHolder.from(parent)

    override fun onBindHeader(holder: PlaylistDetailHeaderViewHolder, parent: Playlist) =
        holder.bind(parent, editedPlaylist, listener)

    /**
     * Indicate to this adapter that editing is ongoing with the current state of the editing
     * process. This will make the header immediately update to reflect information about the edited
     * playlist.
     */
    fun setEditedPlaylist(songs: List<Song>?) {
        if (editedPlaylist == songs) {
            // Nothing to do.
            return
        }
        logD("Updating editing state [old: ${editedPlaylist?.size} new: ${songs?.size}")
        editedPlaylist = songs
        rebindParent()
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays the [Playlist] header in the detail view. Use [from] to
 * create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class PlaylistDetailHeaderViewHolder
private constructor(private val binding: ItemDetailHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param playlist The new [Playlist] to bind.
     * @param editedPlaylist The current edited state of the playlist, if it exists.
     * @param listener A [DetailHeaderAdapter.Listener] to bind interactions to.
     */
    fun bind(
        playlist: Playlist,
        editedPlaylist: List<Song>?,
        listener: DetailHeaderAdapter.Listener
    ) {
        if (editedPlaylist != null) {
            logD("Binding edited playlist image")
            binding.detailCover.bind(
                editedPlaylist,
                binding.context.getString(R.string.desc_playlist_image, playlist.name),
                R.drawable.ic_playlist_24)
        } else {
            binding.detailCover.bind(playlist)
        }

        binding.detailType.text = binding.context.getString(R.string.lbl_playlist)
        binding.detailName.text = playlist.name.resolve(binding.context)
        // Nothing about a playlist is applicable to the sub-head text.
        binding.detailSubhead.isVisible = false

        val songs = editedPlaylist ?: playlist.songs
        val durationMs = editedPlaylist?.sumOf { it.durationMs } ?: playlist.durationMs
        // The song count of the playlist maps to the info text.
        binding.detailInfo.text =
            if (songs.isNotEmpty()) {
                binding.context.getString(
                    R.string.fmt_two,
                    binding.context.getPlural(R.plurals.fmt_song_count, songs.size),
                    durationMs.formatDurationMs(true))
            } else {
                binding.context.getString(R.string.def_song_count)
            }

        val playable = playlist.songs.isNotEmpty() && editedPlaylist == null
        if (!playable) {
            logD("Playlist is being edited or is empty, disabling playback options")
        }

        binding.detailPlayButton.apply {
            isEnabled = playable
            setOnClickListener { listener.onPlay() }
        }
        binding.detailShuffleButton.apply {
            isEnabled = playable
            setOnClickListener { listener.onShuffle() }
        }
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            PlaylistDetailHeaderViewHolder(ItemDetailHeaderBinding.inflate(parent.context.inflater))
    }
}
