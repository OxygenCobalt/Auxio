package org.oxycblt.auxio.music.processing

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Albums
import android.provider.MediaStore.Audio.Artists
import android.provider.MediaStore.Audio.Genres
import android.provider.MediaStore.Audio.Media
import android.util.Log
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toAlbumArtURI
import org.oxycblt.auxio.music.toNamedGenre

enum class MusicLoaderResponse {
    DONE, FAILURE, NO_MUSIC
}

// Class that loads music from the FileSystem.
// TODO: Add custom artist images from the filesystem
// TODO: Move genre loading to songs [Loads would take longer though]
class MusicLoader(
    private val resolver: ContentResolver,

    private val genrePlaceholder: String,
    private val artistPlaceholder: String,
    private val albumPlaceholder: String,
) {
    var genres = mutableListOf<Genre>()
    var artists = mutableListOf<Artist>()
    var albums = mutableListOf<Album>()
    var songs = mutableListOf<Song>()

    val response: MusicLoaderResponse

    init {
        response = findMusic()
    }

    private fun findMusic(): MusicLoaderResponse {
        try {
            loadGenres()
            loadArtists()
            loadAlbums()
            loadSongs()
        } catch (error: Exception) {
            Log.e(this::class.simpleName, "Something went horribly wrong.")
            error.printStackTrace()

            return MusicLoaderResponse.FAILURE
        }

        if (songs.size == 0) {
            return MusicLoaderResponse.NO_MUSIC
        }

        return MusicLoaderResponse.DONE
    }

    private fun loadGenres() {
        Log.d(this::class.simpleName, "Starting genre search...")

        // First, get a cursor for every genre in the android system
        val genreCursor = resolver.query(
            Genres.EXTERNAL_CONTENT_URI,
            arrayOf(
                Genres._ID, // 0
                Genres.NAME // 1
            ),
            null, null,
            Genres.DEFAULT_SORT_ORDER
        )

        // And then process those into Genre objects
        genreCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(Genres._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(Genres.NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                var name = cursor.getString(nameIndex) ?: genrePlaceholder

                // If a genre is still in an old int-based format [Android formats it as "(INT)"],mu
                // convert that to the corresponding ID3 genre.
                if (name.contains(Regex("[0123456789)]"))) {
                    name = name.toNamedGenre() ?: genrePlaceholder
                }

                genres.add(
                    Genre(
                        id, name
                    )
                )
            }

            cursor.close()
        }

        Log.d(
            this::class.simpleName,
            "Genre search finished with ${genres.size} genres found."
        )
    }

    private fun loadArtists() {
        Log.d(this::class.simpleName, "Starting artist search...")

        // Load all the artists
        val artistCursor = resolver.query(
            Artists.EXTERNAL_CONTENT_URI,
            arrayOf(
                Artists._ID, // 0
                Artists.ARTIST // 1
            ),
            null, null,
            Artists.DEFAULT_SORT_ORDER
        )

        artistCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(Artists._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(Artists.ARTIST)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                var name = cursor.getString(nameIndex)

                if (name == null || name == MediaStore.UNKNOWN_STRING) {
                    name = artistPlaceholder
                }

                artists.add(
                    Artist(
                        id, name
                    )
                )
            }

            cursor.close()
        }

        artists = artists.distinctBy {
            it.name to it.genres
        }.toMutableList()

        // Then try to associate any genres with their respective artists.
        for (genre in genres) {
            val artistGenreCursor = resolver.query(
                Genres.Members.getContentUri("external", genre.id),
                arrayOf(
                    Genres.Members.ARTIST_ID
                ),
                null, null, null
            )

            artistGenreCursor?.let { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(Genres.Members.ARTIST_ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)

                    artists.filter { it.id == id }.forEach {
                        it.genres.add(genre)
                    }
                }
            }

            artistGenreCursor?.close()
        }

        Log.d(
            this::class.simpleName,
            "Artist search finished with ${artists.size} artists found."
        )
    }

    @SuppressLint("InlinedApi")
    private fun loadAlbums() {
        Log.d(this::class.simpleName, "Starting album search...")

        val albumCursor = resolver.query(
            Albums.EXTERNAL_CONTENT_URI,
            arrayOf(
                Albums._ID, // 0
                Albums.ALBUM, // 1
                Albums.ARTIST_ID, // 2

                Albums.FIRST_YEAR, // 3
            ),
            null, null,
            Albums.DEFAULT_SORT_ORDER
        )

        albumCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(Albums._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(Albums.ALBUM)
            val artistIdIndex = cursor.getColumnIndexOrThrow(Albums.ARTIST_ID)
            val yearIndex = cursor.getColumnIndexOrThrow(Albums.FIRST_YEAR)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex) ?: albumPlaceholder
                val artistId = cursor.getLong(artistIdIndex)
                val year = cursor.getInt(yearIndex)

                val coverUri = id.toAlbumArtURI()

                albums.add(
                    Album(
                        id, name, artistId,
                        coverUri, year
                    )
                )
            }

            cursor.close()
        }

        albums = albums.distinctBy {
            it.name to it.artistId to it.year
        }.toMutableList()

        Log.d(
            this::class.simpleName,
            "Album search finished with ${albums.size} albums found"
        )
    }

    @SuppressLint("InlinedApi")
    private fun loadSongs() {
        Log.d(this::class.simpleName, "Starting song search...")

        val songCursor = resolver.query(
            Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                Media._ID, // 0
                Media.DISPLAY_NAME, // 1
                Media.TITLE, // 2
                Media.ALBUM_ID, // 3
                Media.TRACK, // 4
                Media.DURATION, // 5
            ),
            Media.IS_MUSIC + "=1", null,
            Media.DEFAULT_SORT_ORDER
        )

        songCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(Media._ID)
            val fileIndex = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME)
            val titleIndex = cursor.getColumnIndexOrThrow(Media.TITLE)
            val albumIndex = cursor.getColumnIndexOrThrow(Media.ALBUM_ID)
            val trackIndex = cursor.getColumnIndexOrThrow(Media.TRACK)
            val durationIndex = cursor.getColumnIndexOrThrow(Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val title = cursor.getString(titleIndex) ?: cursor.getString(fileIndex)
                val albumId = cursor.getLong(albumIndex)
                val track = cursor.getInt(trackIndex)
                val duration = cursor.getLong(durationIndex)

                songs.add(
                    Song(
                        id, title, albumId,
                        track, duration
                    )
                )
            }

            cursor.close()
        }

        songs = songs.distinctBy {
            it.name to it.albumId to it.track to it.duration
        }.toMutableList()

        Log.d(
            this::class.simpleName,
            "Song search finished with ${songs.size} found"
        )
    }
}
