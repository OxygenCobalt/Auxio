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
import org.oxycblt.auxio.settings.SettingsManager

// --- BINDING ADAPTERS ---

/**
 * Bind the album art for a [song].
 */
@BindingAdapter("albumArt")
fun ImageView.bindAlbumArt(song: Song?) = load(song, R.drawable.ic_album)

/**
 * Bind the album art for an [album].
 */
@BindingAdapter("albumArt")
fun ImageView.bindAlbumArt(album: Album?) = load(album, R.drawable.ic_album)

/**
 * Bind the image for an [artist]
 */
@BindingAdapter("artistImage")
fun ImageView.bindArtistImage(artist: Artist?) = load(artist, R.drawable.ic_artist)

/**
 * Bind the image for a [genre]
 */
@BindingAdapter("genreImage")
fun ImageView.bindGenreImage(genre: Genre?) = load(genre, R.drawable.ic_genre)

fun <T : Music> ImageView.load(music: T?, @DrawableRes error: Int) {
    dispose()

    // We don't round album covers by default as it desecrates album artwork, but we do provide
    // an option if one wants it.
    // As for why we use clipToOutline instead of coil's RoundedCornersTransformation, the transform
    // uses the dimensions of the image to create the corners, which results in inconsistent corners
    // across loaded cover art.
    val settingsManager = SettingsManager.getInstance()

    if (settingsManager.roundCovers && background == null) {
        setBackgroundResource(R.drawable.ui_rounded_cutout)
        clipToOutline = true
    } else if (!settingsManager.roundCovers && background != null) {
        background = null
        clipToOutline = false
    }

    load(music) {
        error(error)
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
            .size(Size.ORIGINAL)
            .target(
                onError = { onDone(null) },
                onSuccess = { onDone(it.toBitmap()) }
            )
            .build()
    )
}
