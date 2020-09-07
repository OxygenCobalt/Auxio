package org.oxycblt.auxio.coil

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.Coil
import coil.request.ImageRequest
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist
import org.oxycblt.auxio.music.models.Song

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
    val request = if (artist.numAlbums >= 4) {
        val uris = mutableListOf<Uri>()

        for (i in 0..3) {
            uris.add(artist.albums[i].coverUri)
        }

        val fetcher = ArtistImageFetcher(context)

        getDefaultRequest(context, this)
            .data(uris)
            .fetcher(fetcher)
            .error(R.drawable.ic_artist)
            .build()
    } else {
        getDefaultRequest(context, this)
            .data(artist.albums[0].coverUri)
            .error(R.drawable.ic_artist)
            .build()
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
