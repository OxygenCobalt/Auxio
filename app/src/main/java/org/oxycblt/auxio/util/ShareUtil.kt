/*
 * Copyright (c) 2023 Auxio Project
 * ShareUtil.kt is part of Auxio.
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
 
package org.oxycblt.auxio.util

import android.content.Context
import androidx.core.app.ShareCompat
import org.oxycblt.auxio.music.Song

private const val MIME_TYPE_FALLBACK = "audio/*"

/**
 * Show system share sheet to share song
 *
 * @param song the [Song] to share
 */
fun Context.shareSong(song: Song) {
    ShareCompat.IntentBuilder(this)
        .setStream(song.uri)
        .setType(song.mimeType.getRawType())
        .startChooser()
}

/**
 * Show system share sheet to share multiple song
 *
 * @param songs the collection of [Song] to share
 */
fun Context.shareSongs(songs: Collection<Song>) {
    if (songs.isEmpty()) {
        return
    }
    if (songs.size == 1) {
        shareSong(songs.first())
        return
    }
    val type = songs.mapTo(HashSet(songs.size)) {
        it.mimeType.getRawType()
    }.singleOrNull() ?: MIME_TYPE_FALLBACK
    ShareCompat.IntentBuilder(this)
        .apply {
            for (song in songs) {
                addStream(song.uri)
            }
        }
        .setType(type)
        .startChooser()
}
