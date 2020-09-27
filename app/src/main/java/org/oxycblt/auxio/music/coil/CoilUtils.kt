package org.oxycblt.auxio.music.coil

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.Coil
import coil.request.ImageRequest
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song

// Get the cover art for a song or album
@BindingAdapter("coverArt")
fun ImageView.getCoverArt(song: Song) {
    val request = getDefaultRequest(context, this)
        .data(song.album.coverUri)
        .error(R.drawable.ic_song)
        .build()

    Coil.imageLoader(context).enqueue(request)
}

@BindingAdapter("coverArt")
fun ImageView.getCoverArt(album: Album) {
    val request = getDefaultRequest(context, this)
        .data(album.coverUri)
        .error(R.drawable.ic_album)
        .build()

    Coil.imageLoader(context).enqueue(request)
}

// Get the artist image
@BindingAdapter("artistImage")
fun ImageView.getArtistImage(artist: Artist) {
    val request: ImageRequest

    // If there are more than one albums, then create a mosaic of them.
    if (artist.numAlbums >= 4) {
        val uris = mutableListOf<Uri>()

        for (i in 0..3) {
            uris.add(artist.albums[i].coverUri)
        }

        val fetcher = MosaicFetcher(context)

        request = getDefaultRequest(context, this)
            .data(uris)
            .fetcher(fetcher)
            .error(R.drawable.ic_artist)
            .build()
    } else {
        // Otherwise, just get the first cover and use that
        // If the artist doesn't have any albums [Which happens], then don't even bother with that.
        if (artist.albums.isNotEmpty()) {
            request = getDefaultRequest(context, this)
                .data(artist.albums[0].coverUri)
                .error(R.drawable.ic_artist)
                .build()
        } else {
            setImageResource(R.drawable.ic_artist)

            return
        }
    }

    Coil.imageLoader(context).enqueue(request)
}

@BindingAdapter("genreImage")
fun ImageView.getGenreImage(genre: Genre) {
    val request: ImageRequest

    if (genre.numArtists >= 4) {
        val uris = mutableListOf<Uri>()

        // For each artist, get the nth album from them [if possible].
        for (i in 0..3) {
            val artist = genre.artists[i]

            uris.add(
                if (artist.albums.size > i) {
                    artist.albums[i].coverUri
                } else {
                    artist.albums[0].coverUri
                }
            )
        }

        val fetcher = MosaicFetcher(context)

        request = getDefaultRequest(context, this)
            .data(uris)
            .fetcher(fetcher)
            .error(R.drawable.ic_genre)
            .build()
    } else {
        if (genre.artists.isNotEmpty()) {
            request = getDefaultRequest(context, this)
                .data(genre.artists[0].albums[0].coverUri)
                .error(R.drawable.ic_genre)
                .build()
        } else {
            setImageResource(R.drawable.ic_genre)

            return
        }
    }

    Coil.imageLoader(context).enqueue(request)
}

// Get the base request used across the app.
private fun getDefaultRequest(context: Context, imageView: ImageView): ImageRequest.Builder {
    return ImageRequest.Builder(context)
        .crossfade(true)
        .placeholder(android.R.color.transparent)
        .target(imageView)
}
