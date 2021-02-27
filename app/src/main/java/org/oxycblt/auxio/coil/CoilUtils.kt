package org.oxycblt.auxio.coil

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import coil.Coil
import coil.fetch.Fetcher
import coil.request.ImageRequest
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
fun ImageView.bindAlbumArt(song: Song) {
    load(song.album, R.drawable.ic_song, AlbumArtFetcher(context))
}

/**
 * Bind the album art for an [album].
 */
@BindingAdapter("albumArt")
fun ImageView.bindAlbumArt(album: Album) {
    load(album, R.drawable.ic_album, AlbumArtFetcher(context))
}

/**
 * Bind the image for an [artist]
 */
@BindingAdapter("artistImage")
fun ImageView.bindArtistImage(artist: Artist) {
    load(artist, R.drawable.ic_artist, MosaicFetcher(context))
}

/**
 * Bind the image for a [genre]
 */
@BindingAdapter("genreImage")
fun ImageView.bindGenreImage(genre: Genre) {
    load(genre, R.drawable.ic_genre, MosaicFetcher(context))
}

/**
 * Custom extension function similar to the stock coil load extensions, but handles whether
 * to show images and custom fetchers.
 * @param T Any datatype that inherits [BaseModel]
 * @param data The data itself
 * @param error Drawable resource to use when loading failed/should not occur.
 * @param fetcher Required fetcher that uses [T] as its datatype
 */
inline fun <reified T : BaseModel> ImageView.load(
    data: T,
    @DrawableRes error: Int,
    fetcher: Fetcher<T>,
) {
    val settingsManager = SettingsManager.getInstance()

    if (!settingsManager.showCovers) {
        setImageResource(error)
        return
    }

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
fun loadBitmap(context: Context, song: Song, onDone: (Bitmap?) -> Unit) {
    val settingsManager = SettingsManager.getInstance()

    if (!settingsManager.showCovers) {
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
