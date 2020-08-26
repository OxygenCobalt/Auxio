package org.oxycblt.auxio.music.processing

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

            // Then add them to the album
            for (song in albumSongs) {
                song.album = album
                album.songs.add(song)
            }

            unknownSongs.removeAll(albumSongs)
        }

        // Any remaining songs will be added to an unknown album
        if (unknownSongs.size > 0) {

            // Reuse an existing unknown album if one is found
            val unknownAlbum = albums.find { it.title == "" } ?: Album()

            for (song in unknownSongs) {
                song.album = unknownAlbum
                unknownAlbum.songs.add(song)
            }

            unknownAlbum.numSongs = unknownAlbum.songs.size

            albums.add(unknownAlbum)

            Log.d(
                this::class.simpleName,
                "${unknownSongs.size} songs were placed into an unknown album."
            )
        }
    }

    private fun sortAlbumsIntoArtists() {
        Log.d(this::class.simpleName, "Sorting albums into artists...")

        val unknownAlbums = albums.toMutableList()

        for (artist in artists) {
            // Find all albums that match the current artist name
            val artistAlbums = albums.filter { it.artistName == artist.name }

            // Then add them to the artist, along with refreshing the amount of albums
            for (album in artistAlbums) {
                album.artist = artist
                artist.albums.add(album)
            }

            artist.numAlbums = artist.albums.size

            unknownAlbums.removeAll(artistAlbums)
        }

        // Any remaining albums will be added to an unknown artist
        if (unknownAlbums.size > 0) {

            // Reuse an existing unknown artist if one is found
            val unknownArtist = artists.find { it.name == "" } ?: Artist()

            for (album in unknownAlbums) {
                album.artist = unknownArtist
                unknownArtist.albums.add(album)
            }

            unknownArtist.numAlbums = albums.size

            Log.d(
                this::class.simpleName,
                "${unknownAlbums.size} albums were placed into an unknown artist."
            )
        }
    }
}
