package org.oxycblt.auxio.music

import android.util.Log
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist
import org.oxycblt.auxio.music.models.Song


// Sort a list of Song objects into lists of songs, albums, and artists.
fun processSongs(songs: MutableList<Song>) : MutableList<Song> {
    // Eliminate all duplicates from the list
    // excluding the ID, as that's guaranteed to be unique [I think]
    return songs.distinctBy {
        it.name to it.artist to it.album to it.year to it.track to it.duration
    }.toMutableList()
}

// Sort a list of song objects into albums
fun sortIntoAlbums(songs: MutableList<Song>) : MutableList<Album> {
    val songsByAlbum = songs.groupBy { it.album }
    val albumList = mutableListOf<Album>()

    songsByAlbum.keys.iterator().forEach { album ->
        val albumSongs = songsByAlbum[album]
        albumSongs?.let {
            albumList.add(
                Album(albumSongs)
            )
        }
    }

    return albumList
}

// Sort a list of album objects into artists
fun sortIntoArtists(albums: MutableList<Album>) : MutableList<Artist> {
    val albumsByArtist = albums.groupBy { it.artist }
    val artistList = mutableListOf<Artist>()

    albumsByArtist.keys.iterator().forEach { artist ->
        val artistAlbums = albumsByArtist[artist]

        artistAlbums?.let {
            artistList.add(
                Artist(artistAlbums)
            )
        }
    }

    return artistList
}