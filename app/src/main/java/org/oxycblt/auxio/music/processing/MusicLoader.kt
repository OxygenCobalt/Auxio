package org.oxycblt.auxio.music.processing

import android.annotation.SuppressLint
import android.app.Application
import android.provider.MediaStore.Audio.Albums
import android.provider.MediaStore.Audio.Genres
import android.provider.MediaStore.Audio.Media
import androidx.core.database.getStringOrNull
import org.oxycblt.auxio.R
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.logE
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toAlbumArtURI
import org.oxycblt.auxio.music.toNamedGenre

/**
 * Object that loads music from the filesystem.
 */
class MusicLoader(private val app: Application) {
    var genres = mutableListOf<Genre>()
    var albums = mutableListOf<Album>()
    var songs = mutableListOf<Song>()

    private val resolver = app.contentResolver

    fun loadMusic(): Response {
        try {
            loadGenres()
            loadAlbums()
            loadSongs()
        } catch (error: Exception) {
            logE("Something went horribly wrong.")
            error.printStackTrace()

            return Response.FAILED
        }

        if (songs.size == 0) {
            return Response.NO_MUSIC
        }

        return Response.SUCCESS
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

        val genrePlaceholder = app.getString(R.string.placeholder_genre)

        // And then process those into Genre objects
        genreCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(Genres._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(Genres.NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                var name = cursor.getStringOrNull(nameIndex) ?: genrePlaceholder

                // If a genre is still in an old int-based format [Android formats it as "(INT)"],
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
                val artistName = cursor.getString(artistIdIndex) ?: artistPlaceholder
                val year = cursor.getInt(yearIndex)
                val coverUri = id.toAlbumArtURI()

                albums.add(
                    Album(
                        id = id, name = name, artistName = artistName,
                        coverUri = coverUri, year = year
                    )
                )
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

        // Then try to associate any genres with their respective songs
        // This is stupidly inefficient, but I don't have another choice really.
        for (genre in genres) {
            val songGenreCursor = resolver.query(
                Genres.Members.getContentUri("external", genre.id),
                arrayOf(
                    Genres.Members._ID
                ),
                null, null, null
            )

            songGenreCursor?.use { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(Genres.Members._ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)

                    songs.find { it.id == id }?.let {
                        genre.addSong(it)
                    }
                }
            }
        }

        logD("Song search finished with ${songs.size} found")
    }

    enum class Response {
        SUCCESS, FAILED, NO_MUSIC
    }
}
