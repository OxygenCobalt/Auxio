package org.oxycblt.auxio.coil

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import coil.Coil
import coil.request.ImageRequest
import org.oxycblt.auxio.R
import org.oxycblt.auxio.logE
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.SettingsManager

// SettingsManager is lazy-initted to prevent it from being used before its initialized.
val settingsManager: SettingsManager by lazy {
    SettingsManager.getInstance()
}

/**
 * Get a bitmap for a song. onDone will be called when the bitmap is loaded.
 * **Do not use this on the UI elements, instead use the Binding Adapters.**
 * @param context [Context] required
 * @param song Song to load the cover for
 * @param onDone What to do with the bitmap when the loading is finished. Bitmap will be null if loading failed/shouldn't occur.
 */
fun getBitmap(context: Context, song: Song, onDone: (Bitmap?) -> Unit) {
    if (!settingsManager.showCovers) {
        onDone(null)

        return
    }

    val request = ImageRequest.Builder(context)
        .doCoverSetup(context, song)
        .target(onError = { onDone(null) }, onSuccess = { onDone(it.toBitmap()) })
        .build()

    Coil.imageLoader(context).enqueue(request)
}

// --- BINDING ADAPTERS ---

/**
 * Bind the cover art for a song.
 */
@BindingAdapter("coverArt")
fun ImageView.bindCoverArt(song: Song) {
    if (!settingsManager.showCovers) {
        setImageResource(R.drawable.ic_song)
        return
    }

    val request = getDefaultRequest()
        .doCoverSetup(context, song)
        .error(R.drawable.ic_song)
        .build()

    Coil.imageLoader(context).enqueue(request)
}

/**
 * Bind the cover art for an album
 */
@BindingAdapter("coverArt")
fun ImageView.bindCoverArt(album: Album) {
    if (!settingsManager.showCovers) {
        setImageResource(R.drawable.ic_album)
        return
    }

    val request = getDefaultRequest()
        .doCoverSetup(context, album)
        .error(R.drawable.ic_album)
        .build()

    Coil.imageLoader(context).enqueue(request)
}

/**
 * Bind the artist image for an artist.
 */
@BindingAdapter("artistImage")
fun ImageView.bindArtistImage(artist: Artist) {
    if (!settingsManager.showCovers) {
        setImageResource(R.drawable.ic_artist)
        return
    }
    val request: ImageRequest

    // If there is more than one album, then create a mosaic of them.
    if (artist.albums.size >= 4) {
        val uris = mutableListOf<Uri>()

        for (i in 0..3) {
            uris.add(artist.albums[i].coverUri)
        }

        val fetcher = MosaicFetcher(context)

        request = getDefaultRequest()
            .data(uris)
            .fetcher(fetcher)
            .error(R.drawable.ic_artist)
            .build()
    } else {
        // Otherwise, just get the first cover and use that
        // If the artist doesn't have any albums [Which happens], then don't even bother with that.
        if (artist.albums.isNotEmpty()) {
            request = getDefaultRequest()
                .doCoverSetup(context, artist.albums[0])
                .error(R.drawable.ic_artist)
                .build()
        } else {
            setImageResource(R.drawable.ic_artist)

            return
        }
    }

    Coil.imageLoader(context).enqueue(request)
}

/**
 * Bind the genre image for a genre.
 */
@BindingAdapter("genreImage")
fun ImageView.bindGenreImage(genre: Genre) {
    if (!settingsManager.showCovers) {
        setImageResource(R.drawable.ic_genre)
        return
    }

    val request: ImageRequest
    val genreCovers = mutableListOf<Uri>()

    genre.songs.groupBy { it.album }.forEach {
        genreCovers.add(it.key.coverUri)
    }

    if (genreCovers.size >= 4) {
        val fetcher = MosaicFetcher(context)

        request = getDefaultRequest()
            .data(genreCovers.slice(0..3))
            .fetcher(fetcher)
            .error(R.drawable.ic_genre)
            .build()
    } else {
        if (genreCovers.isNotEmpty()) {
            request = getDefaultRequest()
                .doCoverSetup(context, genre.songs[0])
                .error(R.drawable.ic_genre)
                .build()
        } else {
            setImageResource(R.drawable.ic_genre)

            return
        }
    }

    Coil.imageLoader(context).enqueue(request)
}

fun ImageRequest.Builder.doCoverSetup(context: Context, data: BaseModel): ImageRequest.Builder {
    if (data is Artist || data is Genre) {
        logE("doCoverSetup does not support ${data::class.simpleName}")

        return this
    }

    if (settingsManager.useQualityCovers) {
        fetcher(QualityCoverFetcher(context))

        if (data is Song) {
            data(data)
        } else if (data is Album) {
            data(data.songs[0])
        }
    } else {
        if (data is Song) {
            data(data.album.coverUri)
        } else if (data is Album) {
            data(data.coverUri)
        }
    }

    return this
}

/**
 * Get the base request used by the above functions
 * @return The base request
 */
private fun ImageView.getDefaultRequest(): ImageRequest.Builder {
    return ImageRequest.Builder(context)
        .crossfade(true)
        .placeholder(android.R.color.transparent)
        .target(this)
}
