package org.oxycblt.auxio.coil

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.Coil
import coil.request.ImageRequest
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist
import org.oxycblt.auxio.music.models.Song

private var artistImageFetcher: ArtistImageFetcher? = null

// Get the cover art for a song or album
@BindingAdapter("coverArt")
fun ImageView.getCoverArt(song: Song) {
    val request = ImageRequest.Builder(context)
        .data(song.album.coverUri)
        .crossfade(true)
        .placeholder(android.R.color.transparent)
        .error(R.drawable.ic_artist)
        .crossfade(true)
        .target(this)
        .build()

    Coil.imageLoader(context).enqueue(request)
}

@BindingAdapter("coverArt")
fun ImageView.getCoverArt(album: Album) {
    val request = ImageRequest.Builder(context)
        .data(album.coverUri)
        .crossfade(true)
        .placeholder(android.R.color.transparent)
        .error(R.drawable.ic_artist)
        .crossfade(true)
        .target(this)
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

        // Initialize the fetcher if it hasn't been already.
        if (artistImageFetcher == null) {
            artistImageFetcher = ArtistImageFetcher(context)
        }

        // Manually create an image request, as that's the only way to add a fetcher that
        // takes a list of uris AFAIK.
        ImageRequest.Builder(context)
            .data(uris)
            .fetcher(artistImageFetcher!!)
            .crossfade(true)
            .placeholder(android.R.color.transparent)
            .error(R.drawable.ic_artist)
            .target(this)
            .build()
    } else {
        ImageRequest.Builder(context)
            .data(artist.albums[0].coverUri)
            .crossfade(true)
            .placeholder(android.R.color.transparent)
            .error(R.drawable.ic_artist)
            .crossfade(true)
            .target(this)
            .build()
    }

    Coil.imageLoader(context).enqueue(request)
}
