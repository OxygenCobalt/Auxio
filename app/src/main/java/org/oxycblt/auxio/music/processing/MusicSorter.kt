package org.oxycblt.auxio.music.processing

import android.util.Log
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist
import org.oxycblt.auxio.music.models.Genre
import org.oxycblt.auxio.music.models.Song

class MusicSorter(
    val genres: MutableList<Genre>,
    val artists: MutableList<Artist>,
    val albums: MutableList<Album>,
    val songs: MutableList<Song>,

    private val genrePlaceholder: String,
    private val artistPlaceholder: String,
    private val albumPlaceholder: String,
) {
    init {
        sortSongsIntoAlbums()
        sortAlbumsIntoArtists()
        sortArtistsIntoGenres()

        addPlaceholders()
        finalizeMusic()
    }

    private fun sortSongsIntoAlbums() {
        Log.d(this::class.simpleName, "Sorting songs into albums...")

        val unknownSongs = songs.toMutableList()

        for (album in albums) {
            // Find all songs that match the current album title
            val albumSongs = songs.filter { it.albumName == album.name }

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
            val unknownAlbum = albums.find { it.name == "" } ?: Album()

            for (song in unknownSongs) {
                song.album = unknownAlbum
                unknownAlbum.songs.add(song)
            }

            unknownAlbum.finalize()

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

            artist.finalize()

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

            unknownArtist.finalize()

            artists.add(unknownArtist)

            Log.d(
                this::class.simpleName,
                "${unknownAlbums.size} albums were placed into an unknown artist."
            )
        }
    }

    private fun sortArtistsIntoGenres() {
        Log.d(this::class.simpleName, "Sorting artists into genres...")

        val unknownArtists = artists.toMutableList()

        for (genre in genres) {
            // Find all artists that match the current genre
            val genreArtists = artists.filter { artist ->
                artist.genres.any {
                    it.name == genre.name
                }
            }

            // Then add them to the genre, along with refreshing the amount of artists
            genre.artists.addAll(genreArtists)
            genre.finalize()

            unknownArtists.removeAll(genreArtists)
        }

        if (unknownArtists.size > 0) {
            // Reuse an existing unknown genre if one is found
            val unknownGenre = genres.find { it.name == "" } ?: Genre()

            for (artist in unknownArtists) {
                artist.genres.add(unknownGenre)
                unknownGenre.artists.add(artist)
            }

            unknownGenre.finalize()

            genres.add(unknownGenre)

            Log.d(
                this::class.simpleName,
                "${unknownArtists.size} albums were placed into an unknown genre."
            )
        }
    }

    // Correct any empty names [""] with the proper placeholders [Unknown Album]
    private fun addPlaceholders() {
        genres.forEach { if (it.name == "") it.name = genrePlaceholder }
        artists.forEach { if (it.name == "") it.name = artistPlaceholder }
        albums.forEach { if (it.name == "") it.name = albumPlaceholder }
    }

    // Sort all music into
    private fun finalizeMusic() {
        genres.sortBy { it.name }
        artists.sortBy { it.name }
        albums.sortBy { it.name }
    }
}
