/*
 * Copyright (c) 2021 Auxio Project
 * CoilUtils.kt is part of Auxio.
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

package org.oxycblt.auxio.coil

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import coil.dispose
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.size.OriginalSize
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song

// --- BINDING ADAPTERS ---

/**
 * Bind the album art for a [song].
 */
@BindingAdapter("albumArt")
fun ImageView.bindAlbumArt(song: Song?) {
    dispose()

    load(song) {
        error(R.drawable.ic_album)
    }
}

/**
 * Bind the album art for an [album].
 */
@BindingAdapter("albumArt")
fun ImageView.bindAlbumArt(album: Album?) {
    dispose()

    load(album) {
        error(R.drawable.ic_album)
    }
}

/**
 * Bind the image for an [artist]
 */
@BindingAdapter("artistImage")
fun ImageView.bindArtistImage(artist: Artist?) {
    dispose()

    load(artist) {
        error(R.drawable.ic_artist)
    }
}

/**
 * Bind the image for a [genre]
 */
@BindingAdapter("genreImage")
fun ImageView.bindGenreImage(genre: Genre?) {
    dispose()

    load(genre) {
        error(R.drawable.ic_genre)
    }
}

// --- OTHER FUNCTIONS ---

/**
 * Get a bitmap for a [song]. [onDone] will be called with the loaded bitmap, or null if loading
 * failed/shouldn't occur.
 * **This not meant for UIs, instead use the Binding Adapters.**
 */
fun loadBitmap(
    context: Context,
    song: Song,
    onDone: (Bitmap?) -> Unit
) {
    context.imageLoader.enqueue(
        ImageRequest.Builder(context)
            .data(song.album)
            .size(OriginalSize)
            .target(
                onError = { onDone(null) },
                onSuccess = { onDone(it.toBitmap()) }
            )
            .build()
    )
}
