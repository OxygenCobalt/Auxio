package org.oxycblt.auxio.music.processing

import android.annotation.SuppressLint
import android.app.Application
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Albums
import android.provider.MediaStore.Audio.Genres
import android.provider.MediaStore.Audio.Media
import androidx.core.database.getStringOrNull
import org.oxycblt.auxio.R
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.logE
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toAlbumArtURI

/**
 * Class that loads/constructs [Genre]s, [Album]s, and [Song] objects from the filesystem
 * Artists are constructed in [MusicSorter], as they are only really containers for [Album]s
 */
class MusicLoader(private val app: Application) {
    var genres = mutableListOf<Genre>()
    var albums = mutableListOf<Album>()
    var songs = mutableListOf<Song>()

    private val resolver = app.contentResolver

    fun loadMusic(): MusicStore.Response {
        try {
            loadGenres()
            loadAlbums()
            loadSongs()
        } catch (error: Exception) {
            val trace = error.stackTraceToString()

            logE("Something went horribly wrong.")
            logE(trace)

            return MusicStore.Response.FAILED
        }

        if (songs.isEmpty()) {
            return MusicStore.Response.NO_MUSIC
        }

        return MusicStore.Response.SUCCESS
    }

    private fun loadGenres() {
        logD("Starting genre search...")

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
                val name = cursor.getStringOrNull(nameIndex) ?: continue // No non-broken genre would be missing a name

                genres.add(Genre(id, name))
            }

            cursor.close()
        }

        logD("Genre search finished with ${genres.size} genres found.")
    }

    @SuppressLint("InlinedApi")
    private fun loadAlbums() {
        logD("Starting album search...")

        val albumCursor = resolver.query(
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

        val albumPlaceholder = app.getString(R.string.placeholder_album)
        val artistPlaceholder = app.getString(R.string.placeholder_artist)

        albumCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(Albums._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(Albums.ALBUM)
            val artistIdIndex = cursor.getColumnIndexOrThrow(Albums.ARTIST)
            val yearIndex = cursor.getColumnIndexOrThrow(Albums.FIRST_YEAR)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex) ?: albumPlaceholder
                var artistName = cursor.getString(artistIdIndex) ?: artistPlaceholder
                val year = cursor.getInt(yearIndex)
                val coverUri = id.toAlbumArtURI()

                // Correct any artist names to a nicer "Unknown Artist" label
                if (artistName == MediaStore.UNKNOWN_STRING) {
                    artistName = artistPlaceholder
                }

                albums.add(Album(id, name, artistName, coverUri, year))
            }

            cursor.close()
        }

        albums = albums.distinctBy {
            it.name to it.artistName to it.year
        }.toMutableList()

        logD("Album search finished with ${albums.size} albums found")
    }

    @SuppressLint("InlinedApi")
    private fun loadSongs() {
        logD("Starting song search...")

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

                songs.add(Song(id, title, albumId, track, duration))
            }

            cursor.close()
        }

        songs = songs.distinctBy {
            it.name to it.albumId to it.track to it.duration
        }.toMutableList()

        // Then try to associate any genres with their respective songs
        // This is stupidly inefficient, but I don't have another choice really.
        // Blame the android devs for deciding to design MediaStore this way.
        for (genre in genres) {
            val songGenreCursor = resolver.query(
                Genres.Members.getContentUri("external", genre.id),
                arrayOf(Genres.Members._ID),
                null, null, null
            )

            songGenreCursor?.use { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(Genres.Members._ID)

                while (cursor.moveToNext()) {
                    val songId = cursor.getLong(idIndex)

                    songs.find { it.id == songId }?.let {
                        genre.addSong(it)
                    }
                }
            }
        }

        /*
        // Fix that will group songs w/o genres into an unknown genre
        // Currently disabled until it would actually benefit someone, otherwise its just
        // a performance deadweight.
        val songsWithoutGenres = songs.filter { it.genre == null }

        if (songsWithoutGenres.isNotEmpty()) {
            val unknownGenre = Genre(name = app.getString(R.string.placeholder_genre))

            songsWithoutGenres.forEach {
                unknownGenre.addSong(it)
            }

            genres.add(unknownGenre)
        }
         */

        logD("Song search finished with ${songs.size} found")
    }
}
