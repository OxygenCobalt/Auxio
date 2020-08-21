package org.oxycblt.auxio.music

import android.util.Log
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist
import org.oxycblt.auxio.music.models.Song

class MusicSorter(
    val artists: MutableList<Artist>,
    val albums: MutableList<Album>,
    val songs: MutableList<Song>
) {
    init {
        sortSongsIntoAlbums()
        sortAlbumsIntoArtists()
    }

    private fun sortSongsIntoAlbums() {
        Log.d(this::class.simpleName, "Sorting songs into albums...")

        val unknownSongs = songs.toMutableList()

        for (album in albums) {
            // Find all songs that match the current album title
            val albumSongs = songs.filter { it.albumName == album.title }

            // And then add them to the album
            album.songs.addAll(albumSongs)

            unknownSongs.removeAll(albumSongs)
        }

        // Any remaining songs will be added to an unknown album
        if (unknownSongs.size > 0) {

            // Reuse an existing unknown albumif one is found
            val unknownAlbum = albums.find { it.title == null } ?: Album()

            unknownAlbum.songs.addAll(unknownSongs)
            unknownAlbum.numSongs = unknownAlbum.songs.size

            for (song in unknownSongs) {
                song.album = unknownAlbum
            }

            albums.add(unknownAlbum)

            Log.d(
                this::class.simpleName,
                "Placed " + unknownSongs.size.toString() + " songs into an unknown album"
            )
        }
    }

    private fun sortAlbumsIntoArtists() {
        Log.d(this::class.simpleName, "Sorting albums into artists...")

        val unknownAlbums = albums.toMutableList()

        for (artist in artists) {
            // Find all albums that match the current artist name
            val artistAlbums = albums.filter { it.artistName == artist.name }

            // And then add them to the album, along with refreshing the amount of albums
            artist.albums.addAll(artistAlbums)
            artist.numAlbums = artist.albums.size

            unknownAlbums.removeAll(artistAlbums)
        }

        // Any remaining albums will be added to an unknown artist
        if (unknownAlbums.size > 0) {

            // Reuse an existing unknown artist if one is found
            val unknownArtist = artists.find { it.name == null } ?: Artist()

            unknownArtist.albums.addAll(unknownAlbums)
            unknownArtist.numAlbums = albums.size

            for (album in unknownAlbums) {
                album.artist = unknownArtist
            }

            artists.add(unknownArtist)

            Log.d(
                this::class.simpleName,
                "Placed " + unknownAlbums.size.toString() + " albums into an unknown album"
            )
        }
    }
}
