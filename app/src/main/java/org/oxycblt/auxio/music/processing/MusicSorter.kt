package org.oxycblt.auxio.music.processing

import android.util.Log
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song

class MusicSorter(
    var genres: MutableList<Genre>,
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

        // Remove genre duplicates at the end, as duplicate genres can be added during
        // the sorting process as well.
        genres = genres.distinctBy {
            it.name
        }.toMutableList()

        // Also elimate any genres that dont have artists, which also happens sometimes.
        genres.removeAll { it.artists.isEmpty() }
    }

    private fun sortSongsIntoAlbums() {
        Log.d(this::class.simpleName, "Sorting songs into albums...")

        val unknownSongs = songs.toMutableList()

        for (album in albums) {
            // Find all songs that match the current album ID to prevent any bugs w/comparing names.
            // This cant be done anywhere else sadly. Blame the genre system.
            val albumSongs = songs.filter { it.albumId == album.id }

            // Then add them to the album
            for (song in albumSongs) {
                song.album = album
                album.songs.add(song)
            }

            unknownSongs.removeAll(albumSongs)
        }

        // Any remaining songs will be added to an unknown album
        if (unknownSongs.size > 0) {

            val unknownAlbum = Album(
                name = albumPlaceholder
            )

            for (song in unknownSongs) {
                song.album = unknownAlbum
                unknownAlbum.songs.add(song)
            }

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
            val artistAlbums = albums.filter { it.artistId == artist.id }

            // Then add them to the artist, along with refreshing the amount of albums
            for (album in artistAlbums) {
                album.artist = artist
                artist.albums.add(album)
            }

            // Then group the artist's genres and sort them by "Prominence"
            // A.K.A Who has the most bugged duplicate genres
            val groupedGenres = artist.genres.groupBy { it.name }
            artist.genres.clear()

            groupedGenres.keys.sortedByDescending { key ->
                groupedGenres[key]?.size
            }.forEach { key ->
                groupedGenres[key]?.get(0)?.let {
                    artist.genres.add(it)
                }
            }

            unknownAlbums.removeAll(artistAlbums)
        }

        // Any remaining albums will be added to an unknown artist
        if (unknownAlbums.size > 0) {

            // Reuse an existing unknown artist if one is found
            val unknownArtist = Artist(
                name = artistPlaceholder
            )

            for (album in unknownAlbums) {
                album.artist = unknownArtist
                unknownArtist.albums.add(album)
            }

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

            unknownArtists.removeAll(genreArtists)
        }

        if (unknownArtists.size > 0) {
            // Reuse an existing unknown genre if one is found
            val unknownGenre = genres.find { it.name == genrePlaceholder } ?: Genre(
                name = genrePlaceholder
            )

            for (artist in unknownArtists) {
                artist.genres.add(unknownGenre)
                unknownGenre.artists.add(artist)
            }
            genres.add(unknownGenre)

            Log.d(
                this::class.simpleName,
                "${unknownArtists.size} artists were placed into an unknown genre."
            )
        }
    }
}
