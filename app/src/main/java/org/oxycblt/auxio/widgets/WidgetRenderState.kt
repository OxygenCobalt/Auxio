/*
 * Copyright (c) 2026 Auxio Project
 * WidgetRenderState.kt is part of Auxio.
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

package org.oxycblt.auxio.widgets

import androidx.annotation.DrawableRes
import org.oxycblt.auxio.R

sealed class WidgetRenderState {
    data object NoSession : WidgetRenderState()

    data class Active(
        val title: String,
        val subtitle: String,
        val artist: String,
        val albumArtist: String?,
        val album: String?,
        val isPlaying: Boolean,
        val hasArtwork: Boolean,
    ) : WidgetRenderState()

    companion object {
        fun fromPlayback(
            title: String?,
            artist: String?,
            album: String?,
            albumArtist: String? = null,
            isPlaying: Boolean,
            hasArtwork: Boolean,
        ): WidgetRenderState {
            if (title.isNullOrBlank()) return NoSession
            return Active(
                title = title,
                subtitle =
                    listOfNotNull(artist, albumArtist)
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .distinct()
                        .joinToString(" • ")
                        .ifBlank { artist.orEmpty() },
                artist = artist.orEmpty(),
                albumArtist = albumArtist,
                album = album,
                isPlaying = isPlaying,
                hasArtwork = hasArtwork,
            )
        }

        @DrawableRes
        fun playPauseIcon(isPlaying: Boolean): Int = if (isPlaying) R.drawable.ic_pause_24 else R.drawable.ic_play_24

        @DrawableRes
        fun playPauseBackground(isPlaying: Boolean): Int =
            if (isPlaying) R.drawable.ui_remote_fab_container_playing else R.drawable.ui_remote_fab_container_paused
    }
}