/*
 * Copyright (c) 2021 Auxio Project
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
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.graphics.drawable.toBitmap
import coil.dispose
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.size.Size
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song

// --- BINDING ADAPTERS ---

/** Bind the album cover for a [song]. */
fun ImageView.applyAlbumCover(song: Song?) =
    load(song, R.drawable.ic_album, R.string.desc_album_cover)

/** Bind the album cover for an [album]. */
fun ImageView.applyAlbumCover(album: Album?) =
    load(album, R.drawable.ic_album, R.string.desc_album_cover)

/** Bind the image for an [artist] */
fun ImageView.applyArtistImage(artist: Artist?) =
    load(artist, R.drawable.ic_artist, R.string.desc_artist_image)

/** Bind the image for a [genre] */
fun ImageView.applyGenreImage(genre: Genre?) =
    load(genre, R.drawable.ic_genre, R.string.desc_genre_image)

fun <T : Music> ImageView.load(music: T?, @DrawableRes error: Int, @StringRes desc: Int) {
    contentDescription = context.getString(desc, music?.resolvedName)
    dispose()
    scaleType = ImageView.ScaleType.FIT_CENTER
    load(music) {
        error(error)
        transformations(SquareFrameTransform.INSTANCE)
    }
}

// --- OTHER FUNCTIONS ---

/**
 * Get a bitmap for a [song]. [onDone] will be called with the loaded bitmap, or null if loading
 * failed/shouldn't occur. **This not meant for UIs, instead use the Binding Adapters.**
 */
fun loadBitmap(context: Context, song: Song, onDone: (Bitmap?) -> Unit) {
    context.imageLoader.enqueue(
        ImageRequest.Builder(context)
            .data(song.album)
            .size(Size.ORIGINAL)
            .transformations(SquareFrameTransform())
            .target(onError = { onDone(null) }, onSuccess = { onDone(it.toBitmap()) })
            .build())
}
