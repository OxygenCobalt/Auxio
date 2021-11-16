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
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import coil.Coil
import coil.clear
import coil.fetch.Fetcher
import coil.request.ImageRequest
import coil.size.OriginalSize
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.SettingsManager

// --- BINDING ADAPTERS ---

/**
 * Bind the album art for a [song].
 */
@BindingAdapter("albumArt")
fun ImageView.bindAlbumArt(song: Song?) {
    load(song?.album, R.drawable.ic_album, AlbumArtFetcher(context))
}

/**
 * Bind the album art for an [album].
 */
@BindingAdapter("albumArt")
fun ImageView.bindAlbumArt(album: Album?) {
    load(album, R.drawable.ic_album, AlbumArtFetcher(context))
}

/**
 * Bind the image for an [artist]
 */
@BindingAdapter("artistImage")
fun ImageView.bindArtistImage(artist: Artist?) {
    load(artist, R.drawable.ic_artist, MosaicFetcher(context))
}

/**
 * Bind the image for a [genre]
 */
@BindingAdapter("genreImage")
fun ImageView.bindGenreImage(genre: Genre?) {
    load(genre, R.drawable.ic_genre, MosaicFetcher(context))
}

/**
 * Custom extension function similar to the stock coil load extensions, but handles whether
 * to show images and custom fetchers.
 * @param T Any datatype that inherits [BaseModel]. This can be null, but keep in mind that it will cause loading to fail.
 * @param data The data itself
 * @param error Drawable resource to use when loading failed/should not occur.
 * @param fetcher Required fetcher that uses [T] as its datatype
 */
inline fun <reified T : BaseModel> ImageView.load(
    data: T?,
    @DrawableRes error: Int,
    fetcher: Fetcher<T>,
) {
    clear()

    Coil.imageLoader(context).enqueue(
        ImageRequest.Builder(context)
            .target(this)
            .data(data)
            .fetcher(fetcher)
            .error(error)
            .build()
    )
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
    val settingsManager = SettingsManager.getInstance()

    if (!settingsManager.showCovers) {
        onDone(null)
        return
    }

    Coil.imageLoader(context).enqueue(
        ImageRequest.Builder(context)
            .data(song.album)
            .fetcher(AlbumArtFetcher(context))
            .size(OriginalSize)
            .target(
                onError = { onDone(null) },
                onSuccess = { onDone(it.toBitmap()) }
            )
            .build()
    )
}
