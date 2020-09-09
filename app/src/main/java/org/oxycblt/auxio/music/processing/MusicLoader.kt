package org.oxycblt.auxio.music.processing

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Albums
import android.provider.MediaStore.Audio.Artists
import android.provider.MediaStore.Audio.Genres
import android.provider.MediaStore.Audio.Media
import android.util.Log
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist
import org.oxycblt.auxio.music.models.Genre
import org.oxycblt.auxio.music.models.Song
import org.oxycblt.auxio.music.toAlbumArtURI
import org.oxycblt.auxio.music.toNamedGenre

enum class MusicLoaderResponse {
    DONE, FAILURE, NO_MUSIC
}

// Class that loads music from the FileSystem.
class MusicLoader(private val resolver: ContentResolver) {

    var genres = mutableListOf<Genre>()
    var artists = mutableListOf<Artist>()
    var albums = mutableListOf<Album>()
    var songs = mutableListOf<Song>()

    private var genreCursor: Cursor? = null
    private var artistCursor: Cursor? = null
    private var albumCursor: Cursor? = null
    private var songCursor: Cursor? = null

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
        genreCursor = resolver.query(
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
                var name = cursor.getString(nameIndex) ?: ""

                // If a genre is still in an old int-based format [Android formats it as "(INT)"],
                // convert that to the corresponding ID3 genre. Really hope anyone doesn't have
                // a genre that contains parentheses.
                if (name.contains(Regex("[0123456789)]"))) {
                    name = name.toNamedGenre()
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

        // To associate artists with their genres, a new cursor is
        // created with all the artists of that type.
        for (genre in genres) {
            artistCursor = resolver.query(
                Genres.Members.getContentUri("external", genre.id),
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
                    val name = cursor.getString(nameIndex) ?: ""

                    // If an artist has already been added [Which is very likely due to how genres
                    // are processed], add the genre to the existing artist instead of creating a
                    // new one.
                    val existingArtist = artists.find { it.name == name }

                    if (existingArtist != null) {
                        existingArtist.genres.add(genre)
                    } else {
                        artists.add(
                            Artist(
                                id, name,
                                mutableListOf(genre)
                            )
                        )
                    }
                }

                cursor.close()
            }
        }

        // Remove dupes [Just in case]
        artists = artists.distinctBy {
            it.name to it.genres
        }.toMutableList()

        Log.d(
            this::class.simpleName,
            "Artist search finished with ${artists.size} artists found."
        )
    }

    private fun loadAlbums() {
        Log.d(this::class.simpleName, "Starting album search...")

        albumCursor = resolver.query(
            Albums.EXTERNAL_CONTENT_URI,
            arrayOf(
                Albums._ID, // 0
                Albums.ALBUM, // 1
                Albums.ARTIST, // 2

                Albums.FIRST_YEAR, // 3
            ),
            null, null,
            Albums.DEFAULT_SORT_ORDER
        )

        albumCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(Albums._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(Albums.ALBUM)
            val artistIndex = cursor.getColumnIndexOrThrow(Albums.ARTIST)
            val yearIndex = cursor.getColumnIndexOrThrow(Albums.FIRST_YEAR)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex) ?: ""
                val artist = cursor.getString(artistIndex) ?: ""
                val year = cursor.getInt(yearIndex)

                val coverUri = id.toAlbumArtURI()

                albums.add(
                    Album(
                        id, name, artist,
                        coverUri, year
                    )
                )
            }

            cursor.close()
        }

        // Remove dupes
        albums = albums.distinctBy {
            it.name to it.artistName to it.year to it.numSongs
        }.toMutableList()

        Log.d(
            this::class.simpleName,
            "Album search finished with ${albums.size} albums found"
        )
    }

    private fun loadSongs() {
        Log.d(this::class.simpleName, "Starting song search...")

        songCursor = resolver.query(
            Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                Media._ID, // 0
                Media.DISPLAY_NAME, // 1
                Media.TITLE, // 2
                Media.ALBUM, // 4
                Media.TRACK, // 6
                Media.DURATION // 7
            ),
            Media.IS_MUSIC + "=1", null,
            Media.DEFAULT_SORT_ORDER
        )

        songCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(Media._ID)
            val fileIndex = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME)
            val titleIndex = cursor.getColumnIndexOrThrow(Media.TITLE)
            val albumIndex = cursor.getColumnIndexOrThrow(Media.ALBUM)
            val trackIndex = cursor.getColumnIndexOrThrow(Media.TRACK)
            val durationIndex = cursor.getColumnIndexOrThrow(Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val title = cursor.getString(titleIndex) ?: cursor.getString(fileIndex)
                val album = cursor.getString(albumIndex) ?: ""
                val track = cursor.getInt(trackIndex)
                val duration = cursor.getLong(durationIndex)

                songs.add(
                    Song(
                        id, title, album,
                        track, duration
                    )
                )
            }

            cursor.close()
        }

        // Remove dupes
        songs = songs.distinctBy {
            it.name to it.albumName to it.track to it.duration
        }.toMutableList()

        Log.d(
            this::class.simpleName,
            "Song search finished with ${songs.size} found"
        )
    }
}
