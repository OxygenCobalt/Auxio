package org.oxycblt.auxio.coil

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import coil.Coil
import coil.request.ImageRequest
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.SettingsManager

private val ignoreCovers: Boolean get() = !SettingsManager.getInstance().showCovers

/**
 * Get a bitmap for a song. onDone will be called when the bitmap is loaded.
 * **This not meant for UIs, instead use the Binding Adapters.**
 * @param context [Context] required
 * @param song Song to load the cover for
 * @param onDone What to do with the bitmap when the loading is finished. Bitmap will be null if loading failed/shouldn't occur.
 */
fun loadBitmap(context: Context, song: Song, onDone: (Bitmap?) -> Unit) {
    if (ignoreCovers) {
        onDone(null)
        return
    }

    Coil.imageLoader(context).enqueue(
        ImageRequest.Builder(context)
            .data(song.album)
            .fetcher(AlbumArtFetcher(context))
            .target(
                onError = { onDone(null) },
                onSuccess = { onDone(it.toBitmap()) }
            )
            .build()
    )
}

// --- BINDING ADAPTERS ---

/**
 * Bind the album art for a [Song].
 */
@BindingAdapter("albumArt")
fun ImageView.bindAlbumArt(song: Song) {
    if (ignoreCovers) {
        setImageResource(R.drawable.ic_song)
        return
    }

    Coil.imageLoader(context).enqueue(
        ImageRequest.Builder(context)
            .target(this)
            .data(song.album)
            .fetcher(AlbumArtFetcher(context))
            .error(R.drawable.ic_song)
            .build()
    )
}

/**
 * Bind the album art for an [Album].
 */
@BindingAdapter("albumArt")
fun ImageView.bindAlbumArt(album: Album) {
    if (ignoreCovers) {
        setImageResource(R.drawable.ic_album)
        return
    }

    Coil.imageLoader(context).enqueue(
        ImageRequest.Builder(context)
            .target(this)
            .data(album)
            .fetcher(AlbumArtFetcher(context))
            .error(R.drawable.ic_album)
            .build()
    )
}

/**
 * Bind the image for an [Artist]
 */
@BindingAdapter("artistImage")
fun ImageView.bindArtistImage(artist: Artist) {
    if (ignoreCovers) {
        setImageResource(R.drawable.ic_artist)
        return
    }

    Coil.imageLoader(context).enqueue(
        ImageRequest.Builder(context)
            .target(this)
            .data(artist)
            .fetcher(MosaicFetcher(context))
            .error(R.drawable.ic_artist)
            .build()
    )
}

/**
 * Bind the image for a [Genre]
 */
@BindingAdapter("genreImage")
fun ImageView.bindGenreImage(genre: Genre) {
    if (ignoreCovers) {
        setImageResource(R.drawable.ic_genre)
        return
    }

    Coil.imageLoader(context).enqueue(
        ImageRequest.Builder(context)
            .target(this)
            .data(genre)
            .fetcher(MosaicFetcher(context))
            .error(R.drawable.ic_genre)
            .build()
    )
}
